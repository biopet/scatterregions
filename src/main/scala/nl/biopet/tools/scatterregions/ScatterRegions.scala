/*
 * Copyright (c) 2018 Biopet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.biopet.tools.scatterregions

import java.io.File

import nl.biopet.utils.ngs.intervals.BedRecordList
import nl.biopet.utils.tool.ToolCommand

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object ScatterRegions extends ToolCommand[Args] {
  def emptyArgs = Args()
  def argsParser = new ArgsParser(this)

  def main(args: Array[String]): Unit = {
    val cmdArgs = cmdArrayToArgs(args)

    logger.info("Start")

    val regions = cmdArgs.inputRegions match {
      case Some(file) =>
        BedRecordList
          .fromFile(file)
          .sorted
          .validateContigs(cmdArgs.referenceFasta)
      case _ => BedRecordList.fromReference(cmdArgs.referenceFasta)
    }

    val scatters = regions.scatter(cmdArgs.scatterSize,
                                   cmdArgs.combineContigs,
                                   cmdArgs.maxContigsInScatterJob)

    val futures = scatters.zipWithIndex.map {
      case (list, idx) =>
        Future {
          val bedRecords = BedRecordList.fromList(list).sorted
          bedRecords.writeToFile(
            new File(cmdArgs.outputDir, s"scatter-$idx.bed"))
        }
    }

    Await.result(Future.sequence(futures), Duration.Inf)

    logger.info("Done")
  }

  def descriptionText: String =
    """
      |This tool breaks a reference or bed file into smaller scatter regions of equal size. This can be used for processing inside a pipeline.
    """.stripMargin

  def manualText: String =
    s"""
      |This always require a reference fasta with a dict file next to it.
      |If the a bed file is supplied the tool will validate this file to the given reference.
    """.stripMargin

  def exampleText: String =
    s"""
      |Default run:
      |${example("-R", "reference fasta", "-o", "<output dir>")}
      |
      |With scatter size:
      |${example("-R", "reference fasta", "-o", "<output dir>", "-s", "5000000")}
      |
    """.stripMargin
}

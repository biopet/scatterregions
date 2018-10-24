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

import nl.biopet.utils.ngs.intervals.{BedRecord, BedRecordList}
import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

class ScatterRegionsTest extends ToolTest[Args] {
  def toolCommand: ScatterRegions.type = ScatterRegions
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ScatterRegions.main(Array())
    }
  }

  @Test
  def testDefault(): Unit = {
    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 1
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 16571
  }

  @Test
  def testBamFile(): Unit = {
    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath,
            "--bamFile",
            resourcePath("/paired01.bam")))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 1
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 16571
  }

  @Test
  def testScatterSize(): Unit = {
    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath,
            "-s",
            "1000"))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 16
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 16571
  }

  @Test
  def testScatterSizeNoSplit(): Unit = {
    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath,
            "-s",
            "1000",
            "--notSplitContigs"))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 1
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 16571
  }

  @Test
  def testRegionsScatterSize(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    bedFile.deleteOnExit()
    BedRecordList
      .fromList(List(BedRecord("chrQ", 0, 2000), BedRecord("chrQ", 5000, 7000)))
      .writeToFile(bedFile)

    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath,
            "-s",
            "1000",
            "-L",
            bedFile.getAbsolutePath))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 4
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 4000
  }

  @Test
  def testRegionsInput(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    bedFile.deleteOnExit()
    BedRecordList
      .fromList(List(BedRecord("chrQ", 0, 2000), BedRecord("chrQ", 5000, 7000)))
      .writeToFile(bedFile)

    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath,
            "-L",
            bedFile.getAbsolutePath))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 1
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 4000
  }

  @Test
  def testRegionsOverlap(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    bedFile.deleteOnExit()
    BedRecordList
      .fromList(
        List(BedRecord("chrQ", 0, 1000),
             BedRecord("chrQ", 500, 1500),
             BedRecord("chrQ", 1000, 2000),
             BedRecord("chrQ", 5000, 7000)))
      .writeToFile(bedFile)

    val outputDir = File.createTempFile("scatter.", ".test")
    outputDir.delete()
    outputDir.mkdir()
    ScatterRegions.main(
      Array("-R",
            resourcePath("/fake_chrQ.fa"),
            "-o",
            outputDir.getAbsolutePath,
            "-L",
            bedFile.getAbsolutePath))
    val files = outputDir.list().map(new File(outputDir, _))
    files.length shouldBe 1
    files.map(BedRecordList.fromFile(_).length).sum shouldBe 4000
  }
}

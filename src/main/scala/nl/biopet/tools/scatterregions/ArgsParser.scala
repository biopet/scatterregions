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

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]('o', "outputDir")
    .required()
    .action((x, c) => c.copy(outputDir = x))
    .text("Output directory")
  opt[File]('R', "referenceFasta")
    .required()
    .action((x, c) => c.copy(referenceFasta = x))
    .text("Reference fasta file, (dict file should be next to it)")
  opt[Int]('s', "scatterSize")
    .action((x, c) => c.copy(scatterSize = x))
    .text(
      s"Approximately scatter size, tool will make all scatters the same size. default = ${Args().scatterSize}")
  opt[File]('L', "regions")
    .action((x, c) => c.copy(inputRegions = Some(x)))
    .text(
      "If given only regions in the given bed file will be used for scattering")
  opt[Unit]("notCombineContigs")
    .action((_, c) => c.copy(combineContigs = false))
    .text("If set each scatter can only contain 1 contig")
  opt[Int]("maxContigsInScatterJob")
    .action((x, c) => c.copy(maxContigsInScatterJob = Some(x)))
    .text("If set each scatter can only contain 1 contig")
}

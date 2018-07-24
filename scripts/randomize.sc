import scala.io.Source
import java.io.PrintWriter

// a two-column, tab-delimited file,
// giving citation reference and text contents
val f = "data/hdt-mashup.tsv"

// recursively allocate words from wordList in chunks
// defined by sizesList.  Accumulate them in results
// until done.
def allocateWords (sizesList: Vector[Int], wordList: Vector[String], results:  Vector[String]) : Vector[String] = {
  if (sizesList.isEmpty) {
    results
  } else {
    val updated = results :+ wordList.take(sizesList(0)).mkString(" ")
    allocateWords(sizesList.tail, wordList.drop(sizesList(0)) , updated)
  }
}

// Read data from srcName, generate iterations randomly
// reshuffled texts chunked to the same size units as srcName,
// and write results to files named outputName-N.txt
def randomize(outputName: String, iterations: Int =  10, srcName: String = f) : Unit = {
  val lines = Source.fromFile(srcName).getLines.toVector
  val chunkSizes = for (ln <- lines) yield {
    val columns = ln.split("\t")
    columns(1).split(" ").size
  }
  // shuffle up all words in the text
  val allWords = lines.map(_.split(" ")).flatten.filter(_.nonEmpty)

  for (i <- 0 until iterations) {
    val randomWords = scala.util.Random.shuffle(allWords)
    val randomizedChunks = allocateWords(chunkSizes,randomWords, Vector.empty[String])
    new java.io.PrintWriter(s"randomized/${outputName}-${i}.txt"){write(randomizedChunks.mkString("\n")); close;}
  }
}

println("\n\nWrite a file with randomized text:")
println("\n\trandomize(\"OUTPUTFILE\", ITERATIONS, \"SOURCEFILE\")\n")
println("or to work with default input from Herodotus in 10 iterations,")
println("\n\trandomize(\"OUTPUTFILE\")\n")
println("Results are written in the \"randomized\" directory to files")
println("named OUTPUTFILE-n.txt where n is the iteration." )

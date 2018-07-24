import scala.io.Source
import java.io.PrintWriter

// Default input:
// a two-column, tab-delimited file,
// giving citation reference and text contents
val f = "data/hdt-mashup.tsv"


// structure for recording number of words in a passage
case class ChunkCount(passageID: String, numberWords: Int)

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
    val wordCount = columns(1).split(" ").size
    ChunkCount(columns(0), wordCount)
  }
  // shuffle up all words in the text
  val allWords = lines.map( _.split(" ")).flatten.filter(_.nonEmpty)

  for (i <- 0 until iterations) {
    val randomWords = scala.util.Random.shuffle(allWords)
    val randomizedChunks = allocateWords(chunkSizes.map(_.numberWords),randomWords, Vector.empty[String])

    // zip citations together with new random chunks:
    val citedRandom = chunkSizes.map(_.passageID) zip randomizedChunks
    val citedRandomString = citedRandom.map{ case (id,txt) => s"${id}\t${txt}"}
    new java.io.PrintWriter(s"randomized/${outputName}-${i}.tsv"){write(citedRandomString.mkString("\n")); close;}
  }
}

println("\n\nWrite a file with randomized text:")
println("\n\trandomize(\"OUTPUTFILE\", ITERATIONS, \"SOURCEFILE\")\n")
println("or to work with default input from Herodotus in 10 iterations,")
println("\n\trandomize(\"OUTPUTFILE\")\n")
println("Results are written in the \"randomized\" directory to files")
println("named OUTPUTFILE-n.txt where n is the iteration." )

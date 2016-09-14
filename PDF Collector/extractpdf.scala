/*
Author: Ganesh Nagarajan
Purpose: This program reads all pdf files from the given directory and
convert them into plain text and saves with filename.pdf.txt.
Rejects the images and tables. Howerver the data is still retained.
Uses pdfbox library from apache commons.
*/
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io._

object ExtractPDF {
  /* Return reference to all files in the folder as java.io.file*/
  def getFiles(directory : String) : Array[java.io.File] = {
    try {
      val fileDirectory = (new File(directory)).listFiles()
      return fileDirectory
    } catch {
      case t: Throwable => {t.printStackTrace(); return new Array[java.io.File](0)}
    }  
  }
  
  /* extract the pdf, write it to a text file */
  def writepdf(file : java.io.File){
     try{
        val test = file.toString().split("/").toList.last.concat(".txt")
        println(test)
        val document=PDDocument.load(file)
        val stripper = new PDFTextStripper()
        val doc = stripper.getText(document)
        document.close()
        val bw = new BufferedWriter(new FileWriter(test))
        bw.write(doc)
        bw.close()
     } catch{
       case t: Throwable => None
       /* Ignore the parsing errors. These errors can be due to unsupported encoding/images */
     }
  }
  
  //Orchestrate the extraction
  def extractPDF (files : Array[java.io.File]){
    try {
      for (file <- files) yield{
        writepdf(file)
      }
    }
    catch{
      case t: Throwable => None
    }
  }
  
  //Get the arguments from comandline for the folder location and run the script.
  def main(args: Array[String]){
	for(arg<-args)
		print(arg)
    val files=getFiles(args(0))
    val content=extractPDF(files)
  }
}

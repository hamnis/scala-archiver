package object archiver {
  import java.io.File
  
  implicit class FileOps(val file: File) extends AnyVal {
    def /(name: String): File = new File(file, name)
  }
}

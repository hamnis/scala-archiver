package archiver

import java.io.File

sealed abstract class Packaging(val extension: String)
object Packaging {
  case object Zip extends Packaging("zip")
  case object Directory extends Packaging("")
  case object TarGz extends Packaging("tgz")

  def apply(file: File) = {    
    IO.extension(file).getOrElse("") match {
      case Zip.extension => Zip
      case TarGz.extension => TarGz
      case Directory.extension => Directory
      case z => sys.error("Unknown packaging" + z)
    }
  }
}

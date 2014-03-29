package archiver

import java.io.File

sealed trait Packaging

object Packaging extends ((File) => Packaging){
  case object Zip extends Packaging
  case object Directory extends Packaging
  case object TarGz extends Packaging

  def apply(file: File): Packaging =
    if (file.isDirectory || IO.extension(file).isEmpty) {
      Directory
    } else {
      IO.extension(file).getOrElse("") match {
        case "zip"            => Zip
        case "tar.gz" | "tgz" => TarGz
        case z                => sys.error("Unknown packaging: " + z)
      }
    }
}

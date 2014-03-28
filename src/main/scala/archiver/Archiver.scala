package archiver

import java.io.File
import org.slf4j.LoggerFactory

trait Logging {
  val logger = LoggerFactory.getLogger(getClass)
}

object Archiver {
  def apply(packaging: Packaging): Archiver = {
    packaging match {
      case Packaging.Directory => DirectoryArchiver
      case Packaging.Zip => ZipArchiver
      case Packaging.TarGz => TarArchiver
      case _ => sys.error("Unsupported atm")
    }
  }
}

trait Archiver extends Logging {
  def create(mapping: FileMapping, output: File): File
}

object DirectoryArchiver extends Archiver {
  def create(mapping: FileMapping, output: File) = {
    IO.createDirectory(output)
    mapping.foreach { case (name, file) =>
      val target = output / name
      IO.copy(file, target)
    }
    logger.warn("Ignoring permissions")
    output
  }
}


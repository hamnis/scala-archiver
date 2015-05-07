package archiver

import java.io.File

trait Logger {
  def debug(msg: String): Unit
  def info(msg: String): Unit
}

object Logger {
  implicit object ConsoleLogger extends Logger {
    def debug(msg: String) = Console.out.println("[DEBUG] %s".format(msg))
    def info(msg: String) = Console.out.println("[INFO] %s".format(msg))
  }
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

trait Archiver {
  def create(mapping: FileMapping, output: File)(implicit logger: Logger): File
}

object DirectoryArchiver extends Archiver {
  def create(mapping: FileMapping, output: File)(implicit logger: Logger) = {
    if (output.exists) {
      IO.delete(output)
    }
    IO.createDirectory(output)
    mapping.foreach { case (name, file) =>
      val target = output / name
      IO.copy(file, target)
    }
    logger.debug("Copied all files to " + output)
    logger.debug("Setting permissions")
    mapping.permissions.foreach{ case (name, p) =>
      val target = output / name
      target.setPermissions(p)
    }
    logger.debug("Done setting permissions")    
    output
  }
}


package archiver

import java.io.{FileOutputStream, BufferedOutputStream, File}
import org.apache.commons.compress.archivers.{ArchiveOutputStream, ArchiveEntry}
import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipArchiveOutputStream}
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.compress.compressors.{CompressorStreamFactory, CompressorOutputStream}

trait CommonsArchiver extends Archiver {
  type ZE <: ArchiveEntry
  type ZO <: ArchiveOutputStream
  
  def create(mapping: FileMapping, output: File)(implicit logger: Logger): File = {
    val containerDirectory = output.getAbsoluteFile.getParentFile
    if (!containerDirectory.exists) {
      IO.createDirectory(containerDirectory)
    }

    val entries = mapping.map { case (path, f) =>
      entry(path, f, mapping.permissions)      
    }
    val temp = File.createTempFile(output.getName, "tmp")
    write(temp, entries)
    postProcess(temp, output)
    logger.info("Wrote " + output)
    output
  }

  def entry(path: String, file: File, permissionMap: Map[String, FilePermissions]): (File, ZE)
  def outputStream(file: File): ZO

  def write(output: File, entries: Iterable[(File, ZE)]) {
    val zo = outputStream(output)
    try {
      entries.foreach{case (f, e) =>
        zo.putArchiveEntry(e)
        if (f.isFile) {
          IO.copy(f, zo)
        }
        zo.closeArchiveEntry()
      }  
    } finally {
      zo.close()
    }
  }
  
  def postProcess(temp:File, output: File): Unit = {
    IO.move(temp, output)
  }
}

object ZipArchiver extends CommonsArchiver {
  type ZE = ZipArchiveEntry
  type ZO = ZipArchiveOutputStream

  def entry(path: String, file: File, permissionMap: Map[String, FilePermissions]) = {
    val e = new ZipArchiveEntry(file, path)
    val permissions = permissionMap.get(path).getOrElse(file.permissions)
    e.setUnixMode(permissions.toInt)
    (file, e)
  }

  def outputStream(file: File): ZO = {
    new ZipArchiveOutputStream(file)
  }
}

//TODO: generalize
object TarArchiver extends CommonsArchiver {
  type ZE = TarArchiveEntry
  type ZO = TarArchiveOutputStream
  val compressorFactory = new CompressorStreamFactory()

  def entry(path: String, file: File, permissionMap: Map[String, FilePermissions]) = {
    val e = new TarArchiveEntry(file, path)
    val permissions = permissionMap.get(path).getOrElse(file.permissions)
    e.setMode(permissions.toInt)
    (file, e)
  }

  def outputStream(file: File) = {
    new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(file)))
  }

  override def postProcess(temp: File, output: File) {
    val out = new BufferedOutputStream(new FileOutputStream(output))
    val cos = compressorFactory.createCompressorOutputStream(CompressorStreamFactory.GZIP, out)
    try {
      IO.copy(temp, cos)
    } finally {
      cos.close()
    }
    temp.delete
  }
}

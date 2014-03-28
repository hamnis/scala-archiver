package archiver

import java.io.File
import org.apache.commons.compress.archivers.{ArchiveOutputStream, ArchiveEntry}
import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipArchiveOutputStream}
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}

trait CommonsArchiver extends Archiver {
  type ZE <: ArchiveEntry
  
  def create(mapping: FileMapping, output: File): File = {
    val containerDirectory = output.getAbsoluteFile.getParentFile
    if (!containerDirectory.exists) {
      IO.createDirectory(containerDirectory)
    }

    val entries = mapping.map { case (path, f) =>
      entry(path, f, mapping.permissions)      
    }

    write(output, entries)
    
    logger.info("Wrote " + output)
    output
  }

  def entry(path: String, file: File, permissionMap: Map[String, FilePermissions]): (File, ZE)
  def write(output: File, entries: Iterable[(File, ZE)]): Unit
}

object ZipArchiver extends CommonsArchiver {
  type ZE = ZipArchiveEntry

  def entry(path: String, file: File, permissionMap: Map[String, FilePermissions]) = {
    val e = new ZipArchiveEntry(file, path)
    val permissions = permissionMap.get(path).getOrElse(IO.getPermissions(file))
    e.setUnixMode(permissions.toInt)
    (file, e)
  }

  def write(output: File, entries: Iterable[(File, ZE)]) = {
    val os = new ZipArchiveOutputStream(output)
    
    entries.foreach{case (f, e) =>
      os.putArchiveEntry(e)
      if (f.isFile) {
        IO.transfer(f, os)
      }
      os.closeArchiveEntry()
    }
    os.close()
  }
}


object TarArchiver extends CommonsArchiver {
  type ZE = TarArchiveEntry

  def entry(path: String, file: File, permissionMap: Map[String, FilePermissions]) = {
    val e = new TarArchiveEntry(file, path)
    val permissions = permissionMap.get(path).getOrElse(IO.getPermissions(file))
    e.setMode(permissions.toInt)
    (file, e)
  }

  def write(output: File, entries: Iterable[(File, ZE)]) = {
    import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
    import java.io.FileOutputStream

    val temp = File.createTempFile(output.getName, "tmp")
    val tar = new TarArchiveOutputStream(new FileOutputStream(temp))
    try {
      entries.foreach{case (f, e) =>
        tar.putArchiveEntry(e)
        if (f.isFile) {
          IO.transfer(f, tar)
        }
        tar.closeArchiveEntry()
      }  
    } finally {
      tar.close()
    }

    val gzip = new GzipCompressorOutputStream(new FileOutputStream(output))
    try {
      IO.transfer(temp, gzip)
    } finally {
      gzip.close()
    }
    temp.delete
  }
}

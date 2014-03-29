package archiver

import collection.JavaConverters._
import java.io.{File, OutputStream}
import java.nio.file._
import java.nio.file.attribute.PosixFilePermission

object IO {
  def listFiles(file: File): List[File] = {
    if (file.isDirectory) {
      val stream = Files.newDirectoryStream(file.toPath)
      try {
        stream.iterator.asScala.map(_.toFile).toList
      } finally {
        stream.close()
      }
    } else Nil
  }

  def extension(file: File): Option[String] = extension(file.getName)

  def extension(name: String): Option[String] = {
    val afterLastSlash = name.substring(name.lastIndexOf('/') + 1)
    val afterLastBackslash = afterLastSlash.lastIndexOf('\\') + 1
    val dotIndex = afterLastSlash.indexOf('.', afterLastBackslash)
    if (dotIndex == -1) None else Some(afterLastSlash.substring(dotIndex + 1))
  }

  def copy(file: File, os: OutputStream) = Files.copy(file.toPath, os)

  def copy(src: File, target: File) = {
    Files.copy(src.toPath, target.toPath, StandardCopyOption.COPY_ATTRIBUTES)
  }

  def getPermissions(file: File): FilePermissions = {
    new FilePermissions(Files.getPosixFilePermissions(file.toPath).asScala.toSet[PosixFilePermission])
  }

  def createDirectory(file: File) = Files.createDirectories(file.toPath)

  def setExecutable(file: File, executable: Boolean) = {    
    val perms = getPermissions(file)
    val updated = if (executable) {
      perms.add(FilePermissions.exec)  
    }
    else {
      perms.remove(FilePermissions.exec)
    }
    
    Files.setPosixFilePermissions(file.toPath, updated.permissions.asJava)
  }
}

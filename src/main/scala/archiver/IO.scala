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

  def extension(file: File): Option[String] = {
    val name = file.getName
    val dotIndex = name.lastIndexOf(".")
    if (dotIndex != -1) Some(name.substring(dotIndex + 1)) else None 
  }

  def transfer(file: File, os: OutputStream) = Files.copy(file.toPath, os)

  def copy(src: File, target: File) = {
    Files.copy(src.toPath, target.toPath, StandardCopyOption.COPY_ATTRIBUTES)
  }

  def getPermissions(file: File): FilePermissions = {
    new FilePermissions(Files.getPosixFilePermissions(file.toPath).asScala.toSet[PosixFilePermission])
  }

  def createDirectory(file: File) = Files.createDirectories(file.toPath)

  def setExecutable(file: File) = {
    val p = FilePermissions(Integer.decode("0755"))
    p.foreach(perm => Files.setPosixFilePermissions(file.toPath, perm.permissions.asJava))
  }
}

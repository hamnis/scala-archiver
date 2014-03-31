package archiver

import collection.JavaConverters._
import java.io.{File, OutputStream}
import java.nio.file._
import java.nio.file.attribute.{PosixFilePermission, BasicFileAttributes}

object IO {
  def listFiles(file: File, glob: Option[String] = None): List[File] = {
    if (file.isDirectory) {
      val stream = glob.map(g => Files.newDirectoryStream(file.toPath, g)).
          getOrElse(Files.newDirectoryStream(file.toPath))
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

  def move(src: File, target: File) {
    Files.move(src.toPath, target.toPath)
  }

  def copy(file: File, os: OutputStream){
    Files.copy(file.toPath, os)
  } 

  def copy(src: File, target: File) {
    if (src.isDirectory) {
      createDirectory(target)
    } else {
      createDirectory(target.getParentFile)
      Files.copy(src.toPath, target.toPath, StandardCopyOption.COPY_ATTRIBUTES)
    }
  }

  def getPermissions(file: File): FilePermissions = {
    new FilePermissions(Files.getPosixFilePermissions(file.toPath).asScala.toSet[PosixFilePermission])
  }
  def setPermissions(file: File, perms: FilePermissions) {
    Files.setPosixFilePermissions(file.toPath, perms.permissions.asJava)
  }

  def createDirectory(file: File) = Files.createDirectories(file.toPath)

  def delete(file: File) {
    Files.walkFileTree(file.toPath, DeletingFileVisitor)
  }

  def setExecutable(file: File, executable: Boolean) = {
    if (Files.isRegularFile(file.toPath)) {
      val perms = getPermissions(file)
      val updated = if (executable) {
        perms.add(FilePermissions.exec)
      }
      else {
        perms.remove(FilePermissions.exec)
      }
      setPermissions(file, perms)
    }
  }
}

object DeletingFileVisitor extends SimpleFileVisitor[Path]() {
  override def visitFile(file: Path, attrs: BasicFileAttributes) = {
     Files.delete(file)
     FileVisitResult.CONTINUE
  }
  override def postVisitDirectory(dir: Path, e: java.io.IOException) = {
    if (e == null) {
      Files.delete(dir)
      FileVisitResult.CONTINUE
    } else {
      // directory iteration failed
      throw e
    }
  }
}

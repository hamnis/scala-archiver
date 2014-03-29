package archiver

import scala.util.Try
import scala.collection.JavaConverters._
import java.nio.file.attribute.{PosixFilePermissions, PosixFilePermission}

case class FilePermissions(permissions: Set[PosixFilePermission]) {
  def toInt = {
    val spec = toString
    Integer.decode(spec.grouped(3).collect(FilePermissions.Mapping).mkString("0", "", ""))
  }

  override val toString = PosixFilePermissions.toString(permissions.asJava)
}

object FilePermissions {
  private val Mapping = Map[String, Char](
    "---" -> '0',
    "--x" -> '1',
    "-w-" -> '2',
    "-wx" -> '3',
    "r--" -> '4',
    "r-x" -> '5',
    "rw-" -> '6',
    "rwx" -> '7'
  )

  def apply(in: String): Option[FilePermissions] = Try { 
    FilePermissions(PosixFilePermissions.fromString(in).asScala.toSet[PosixFilePermission]) 
  }.toOption

  def apply(in: Int): Option[FilePermissions] = {
    val spec = Integer.toOctalString(in).collect(Mapping.reverse).mkString("", "", "")
    apply(spec)
  }
}

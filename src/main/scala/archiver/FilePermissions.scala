package archiver

import scala.util.Try
import scala.collection.JavaConverters._
import java.nio.file.attribute.{PosixFilePermissions, PosixFilePermission}

case class FilePermissions(permissions: Set[PosixFilePermission]) {
  def toInt = {
    val repr = toString
    Integer.decode(repr.grouped(3).collect(FilePermissions.Mapping).mkString("0", "", ""))
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

  private val ReverseMapping: Map[Char, String] = 
    Mapping.foldLeft(Map.empty[Char, String]){case (m, (k,v)) => m.updated(v, k)}

  def apply(in: String): Option[FilePermissions] = Try { 
    FilePermissions(PosixFilePermissions.fromString(in).asScala.toSet[PosixFilePermission]) 
  }.toOption

  def apply(in: Int): Option[FilePermissions] = {
    val octal = java.lang.Integer.toOctalString(in).collect(ReverseMapping).mkString("", "", "")
    apply(octal)
  }
}

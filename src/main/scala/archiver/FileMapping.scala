package archiver

import java.io.File

case class FileMapping private(mappings: Map[String, File], permissions: Map[String, FilePermissions]) {
  def foreach(f: ((String, File)) => Unit): Unit = mappings.foreach(f)
  def map[B](f: ((String, File)) => B): Iterable[B] = mappings.map(f)
}

object FileMapping {
  def apply(root: File, permissions: Map[String, FilePermissions] = Map.empty): FileMapping = {
    def entries(f: File): Seq[File] = f :: IO.listFiles(f).flatMap(entries)
    def strip(root: File) = (f: File) => f.getAbsolutePath.substring(root.getAbsolutePath.length).drop(1) -> f
    
    val mappings = entries(root).tail.map(strip(root)).toMap
    new FileMapping(mappings, applyPermissions(mappings, permissions))
  }

  private def applyPermissions(mappings: Map[String, File], permissions: Map[String, FilePermissions]) = {
    permissions.flatMap { case (path, perm) => 
      val actualKey = {
        val noStar = if (path.endsWith("*")) path.dropRight(1) else path
        if (noStar.startsWith("/")) noStar.drop(1) else noStar
      }
      mappings.flatMap {case (path2, _) => if (path2.startsWith(actualKey)) Map(path2 -> perm) else Map.empty[String, FilePermissions]}
    }.toMap
  }
}

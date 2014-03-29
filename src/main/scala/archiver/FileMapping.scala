package archiver

import java.io.File

case class FileMapping(mappings: Map[String, File], permissions: Map[String, FilePermissions]) {
  def size = mappings.size
  def isEmpty = mappings.isEmpty
  def append(fm: FileMapping) = copy(mappings = mappings ++ fm.mappings, permissions = permissions ++ fm.permissions)
  def foreach(f: ((String, File)) => Unit): Unit = mappings.foreach(f)
  def map[B](f: ((String, File)) => B): Iterable[B] = mappings.map(f)
}

object FileMapping {
  def apply(roots: List[File], glob: Option[String] = None, base: Option[String] = None, permissions: Map[String, FilePermissions] = Map.empty): FileMapping = {
    def name(f: File, root: File) = base.map(_ + "/").getOrElse("") + f.getAbsolutePath.substring(root.getAbsolutePath.length).drop(1)
    def entries(f: File): Seq[File] = f :: IO.listFiles(f, glob).flatMap(entries)
    def tuple(root: File) = (f: File) => name(f, root) -> f
    
    val mappings = roots.flatMap(root => entries(root).tail.map(tuple(root))).toMap
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

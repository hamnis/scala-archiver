package archiver

import java.io.File

class FileMapping private(val mappings: Map[String, File], val permissions: Map[String, FilePermissions]) {
  def size = mappings.size
  def isEmpty = mappings.isEmpty
  def append(fm: FileMapping) = new FileMapping(mappings ++ fm.mappings, permissions ++ fm.permissions)
  def foreach(f: ((String, File)) => Unit): Unit = mappings.foreach(f)
  def map[B](f: ((String, File)) => B): Iterable[B] = mappings.map(f)
}

object FileMapping {
  def apply(roots: List[File], glob: Option[String] = None, base: Option[String] = None, permissions: Map[String, FilePermissions] = Map.empty)(implicit resolver: PermissionResolver): FileMapping = {
    def name(f: File, root: File) = base.map(_ + "/").getOrElse("") + f.getAbsolutePath.substring(root.getAbsolutePath.length).drop(1)
    def entries(f: File): Seq[File] = f :: IO.listFiles(f, glob).flatMap(entries)
    def tuple(root: File) = (f: File) => name(f, root) -> f
    
    val mappings = roots.flatMap(root => entries(root).tail.map(tuple(root))).toMap
    new FileMapping(mappings, resolver.resolve(mappings, permissions))
  }

  def apply(mappings: Map[String, File], permissions: Map[String, FilePermissions])(implicit resolver: PermissionResolver) = {
    new FileMapping(mappings, resolver.resolve(mappings, permissions))
  }
}

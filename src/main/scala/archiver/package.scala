package object archiver {
  import java.io.File
  
  implicit class FileOps(val file: File) extends AnyVal {
    def /(name: String): File = new File(file, name)
    def permissions: FilePermissions = IO.getPermissions(file)
    def setPermissions(p: FilePermissions) = IO.setPermissions(file, p)
    def setExecutable(enable: Boolean) = IO.setExecutable(file, enable)
  }

  implicit class MapOps[K, V](val map: Map[K, V]) extends AnyVal {
    def reverse: Map[V, K] = map.foldLeft(Map.empty[V, K]){case (m, (k,v)) => m.updated(v, k)}
  }
}

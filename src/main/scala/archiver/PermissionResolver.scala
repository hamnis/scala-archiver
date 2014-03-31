package archiver

import java.io.File

trait PermissionResolver {
  def resolve(mappings: Map[String, File], permissions: Map[String, FilePermissions]): Map[String, FilePermissions]
}

object PermissionResolver {
  implicit object Default extends PermissionResolver {
    def resolve(mappings: Map[String, File], permissions: Map[String, FilePermissions]): Map[String, FilePermissions] = {
      permissions.flatMap { case (path, perm) => 
        val actualKey = {
          val noStar = if (path.endsWith("*")) path.dropRight(1) else path
          if (noStar.startsWith("/")) noStar.drop(1) else noStar
        }
        mappings.flatMap {case (path2, _) => if (path2.startsWith(actualKey)) Map(path2 -> perm) else Map.empty[String, FilePermissions]}
      }.toMap
    }  
  }
}

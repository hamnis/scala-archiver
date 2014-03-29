package archiver

import org.scalatest._
import java.nio.file.attribute.PosixFilePermission._

class FilePermissionsTest extends FunSuite with Matchers {
  test("Parse file permission 0777") {
    val all = Integer.decode("0777")
    FilePermissions(all) should be (FilePermissions("rwxrwxrwx"))
    FilePermissions("rwxrwxrwx").get.toInt should be (all)
  }
  test("Only read-writeable for user is 0600") {
    val expected = Integer.decode("0600")
    FilePermissions(expected) should be (FilePermissions("rw-------"))
    FilePermissions(expected) should be (Some(FilePermissions(Set(OWNER_READ, OWNER_WRITE))))
  }
  test("Default Directory permissions of mask 022") {
    val expected = Integer.decode("0755")
    FilePermissions(expected) should be (FilePermissions("rwxr-xr-x"))
    FilePermissions(expected) should be (Some(FilePermissions(Set(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ, GROUP_EXECUTE, OTHERS_READ, OTHERS_EXECUTE))))
  }
}

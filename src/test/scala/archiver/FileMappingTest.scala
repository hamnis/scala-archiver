package archiver

import org.scalatest._
import java.io.File

class FileMappingTest extends FunSuite with Matchers {
  test("File mapping in src/main") {
    val f = new File(getRoot(), "src/main")
    val mapping = FileMapping(List(f))
    assert(!mapping.isEmpty)
    mapping.foreach{case (n, _) => n should startWith ("scala") }
  }
  
  test("File mapping in src/main and src/test") {
    val root = getRoot()
    val mapping = FileMapping(List(new File(root, "src/main"), new File(root, "src/test")))
    assert(!mapping.isEmpty)
    mapping.foreach{case (n, _) => n should startWith ("scala") }
  }

  test("File mapping in src/main with custom root ") {
    val f = new File(getRoot(), "src/main")
    val mapping = FileMapping(List(f), base = Some("custom"))
    assert(!mapping.isEmpty)
    mapping.foreach{case (n, _) => n should startWith ("custom/scala") }
  }


  def getRoot() = {
    var parent = new File(getClass.getProtectionDomain.getCodeSource.getLocation.toURI).getParentFile
    while(!new File(parent, "build.sbt").exists) {
      parent = parent.getParentFile
    }
    parent
  }
}

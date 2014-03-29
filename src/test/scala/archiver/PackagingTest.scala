package archiver

import org.scalatest._

class PackagingTest extends FunSuite with Matchers {
  test("Test commons extensions") {
    val map = Map(
      "file.tar.gz" -> "tar.gz",
      "file.zip" -> "zip",
      "no-ext" -> ""
    )
    map.foreach{
      case (name, ext) => IO.extension(name).getOrElse("") should be (ext)
    }
  }
}

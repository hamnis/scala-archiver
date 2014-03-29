package archiver

import java.io.File

object Main extends App {
  if (args.isEmpty || args(0).trim == "--help") {
    println("Usage: <target> --base=<base> <sources>")
    println("example: target/foo.tar.gz /path/to/files")
    println("example: target/foo.tar.gz --base=custom /path/to/files")
    sys.exit(1)
  }

  val target = new File(args(0))
  val archiver = Archiver(Packaging(target))
  val base = getBase()
  val roots = args.drop(if (base.isDefined) 2 else 1).map(new File(_).getAbsoluteFile).toList
  archiver.create(FileMapping(roots, base), target)


  def getBase(): Option[String] = {
    if (args.length >= 2) {
      Some(args(1)).flatMap{s =>
        val idx = s.indexOf("--base=")
        if (idx < 0) None else Some(s.substring("--base=".length))
      }
    } else {
      None
    }
  }
}

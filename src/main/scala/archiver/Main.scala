package archiver

import java.io.File

object Main extends App {
  if (args.isEmpty || args(0).trim == "--help") {
    println("Usage: <target> --base=<base> --glob=<pattern> <sources>")
    println("The glob pattern is found here: http://bit.ly/O8dyr5")
    println("example: target/foo.tar.gz /path/to/files")
    println("example: target/foo.tar.gz --base=custom --glob=*.java /path/to/files")
    println("example: target/foo.tar.gz --base=custom /path/to/files")
    println("example: target/foo.tar.gz --glob=*.java /path/to/files")
    sys.exit(1)
  }

  val target = new File(args(0))
  val archiver = Archiver(Packaging(target))
  val (baseRest, base) = getOption(args.drop(1), "base")
  val (globRest, glob) = getOption(baseRest, "glob")
  val roots = globRest.map(new File(_).getAbsoluteFile).toList
  archiver.create(FileMapping(roots, glob = glob, base = base), target)


  def getOption(args: Array[String], name: String): (Array[String], Option[String]) = {    
    val optName = "--" + name + "="
    val opt = args.headOption.flatMap{s => 
      val idx = s.indexOf(optName)
      if (idx < 0) None else Some(s.substring(optName.length))
    }

    if (opt.isDefined) args.drop(1) -> opt else args -> opt    
  }
}

object Main {

  val usage = """
    Usage:  --ID groupName
                  Your group specific identification
            --IP simulationServerIP
                  The ip address of the simulation server
              """

  val id_arg = 'ID
  val ip_arg = 'IP

  def parseOptions(args: List[String], required: List[Symbol], optional: Map[String, Symbol], options: Map[Symbol, String]): Map[Symbol, String] = {
    args match {
      // Empty list
      case Nil => options

      // Keyword arguments
      case key :: value :: tail if optional.get(key) != None =>
        parseOptions(tail, required, optional, options ++ Map(optional(key) -> value))

      // Positional arguments
      case value :: tail if required != Nil =>
        parseOptions(tail, required.tail, optional, options ++ Map(required.head -> value))

      // Exit if an unknown argument is received
      case _ =>
        printf("unknown argument(s): %s\n", args.mkString(", "))
        sys.exit(1)
    }
  }

  def main(args: Array[String]) {
    // Required positional arguments by key in options
    val required = List('ID, 'IP)

    // Optional arguments by flag which map to a key in options
    val optional = Map("--ID" -> 'ID, "--IP" -> 'IP)

    // Parse options based on the command line args
    val options = parseOptions(args.toList, required, optional, Map())

    new RobolabTestSpec(options(id_arg), options(ip_arg)).execute(color = false)
    sys.exit(1)
  }
}

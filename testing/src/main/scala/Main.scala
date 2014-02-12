/*
 * RobolabSim
 * Copyright (C) 2014 Max Leuthaeuser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

object Main
{
  val usage = """
    Usage:  --ID groupName
                  Your group specific identification
            --IP simulationServerIP
                  The ip address of the simulation server
              """

  def parseOptions(
    args: List[String],
    required: List[Symbol],
    optional: Map[String, Symbol],
    options: Map[Symbol, String]): Map[Symbol, String] =
  {
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
        printf("Unknown argument(s): %s\n", args.mkString(", "))
        sys.exit(1)
    }
  }

  def main(args: Array[String])
  {
    // Required positional arguments by key in options
    val required = List('ID, 'IP)

    // Optional arguments by flag which map to a key in options
    val optional = Map("--ID" -> 'ID, "--IP" -> 'IP)

    // Parse options based on the command line args
    val options = parseOptions(args.toList, required, optional, Map())

    val stdout = Console.out
    val stderr = Console.err
    val stream = new java.io.ByteArrayOutputStream()
    Console.setOut(stream)
    Console.setErr(stream)

    val test = new RobolabTestSpec(options('ID), options('IP))
    test.execute(color = false)
    val result = stream.toString
    test.fixture.client.sendTest(result)

    Console.setErr(stderr)
    Console.setOut(stdout)

    println("+---------------+")
    println("| Upload result |")
    println("+---------------+\n")
    test.fixture.client.sendTest(result)
    println("\n\n")

    println("+---------------+")
    println("| Test results  |")
    println("+---------------+\n")

    println(result)

    sys.exit(1)
  }
}

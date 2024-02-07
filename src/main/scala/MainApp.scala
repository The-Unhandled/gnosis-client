package xyz.forsaken.gnosisclient

import zio.*

import java.io.IOException

/**
 * @author Petros Siatos
 */
object MainApp extends ZIOAppDefault {
  def run: IO[IOException, Unit] =
    Console.printLine("Hello, World!")
}

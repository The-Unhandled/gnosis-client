package xyz.forsaken.gnosisclient

import zio.config.typesafe.TypesafeConfigProvider
import zio.logging.{ConsoleLoggerConfig, LogFilter, LogFormat, consoleLogger}
import zio.test.{TestEnvironment, ZIOSpecDefault, testEnvironment}
import zio.{LogLevel, Runtime, ZLayer}

/**
 * @author Petros Siatos
 */
trait DefaultSpec extends ZIOSpecDefault:

  override val bootstrap: ZLayer[Any, Any, TestEnvironment] =
    testEnvironment ++ (Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    ) >>> consoleLogger(ConsoleLoggerConfig(LogFormat.colored, LogFilter.LogLevelByNameConfig.default)))

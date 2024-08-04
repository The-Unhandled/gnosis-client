package xyz.forsaken.gnosisclient
package validators

import zio.Config
import zio.config.magnolia.deriveConfig

/** @author
  *   Petros Siatos
  */
final case class ValidatorsConfig(indices: Set[Int])

object ValidatorsConfig:
  val config: Config[ValidatorsConfig] =
    deriveConfig[ValidatorsConfig].nested("Validators")

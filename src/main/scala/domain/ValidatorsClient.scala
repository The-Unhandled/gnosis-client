package xyz.forsaken.gnosisclient
package domain

import validators.Validator

import zio.*

/**
 * @author Petros Siatos
 */
trait ValidatorsClient:
  def getValidators: Task[Set[Validator]]

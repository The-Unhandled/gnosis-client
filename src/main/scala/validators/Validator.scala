package xyz.forsaken.gnosisclient
package validators

/**
 * @author Petros Siatos
 */
case class Validator (index: Long, balance: Long, address: String, status: String):
  def print: String =  s"$index - ${balance}GNO - $status"

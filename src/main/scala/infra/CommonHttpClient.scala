package xyz.forsaken.gnosisclient
package infra

import com.github.plokhotnyuk.jsoniter_scala.core.*
import zio.http.*
import zio.{Scope, Task, ZIO}

import java.net.URI
import javax.naming.ConfigurationException

/**
 * @author Petros Siatos
 */
trait CommonHttpClient:

  protected val uri: URI
  protected val apiKey: String

  protected[infra] def getUrl: Task[URL] =
    for {
      url <- ZIO.fromOption(URL.fromURI(uri))
        .orElseFail(new ConfigurationException("Invalid URL"))
      urlWithApiKey = url.addQueryParams(QueryParams("apikey" -> apiKey))
    } yield urlWithApiKey

  protected[infra] def request[T](url: URL)(implicit codec:  JsonValueCodec[T]): ZIO[Client & Scope, Throwable, T] =
    for
      httpClient <- ZIO.service[Client]
      response <- httpClient.request(Request.get(url))
      responseBody <- response.body.asString
      response <- ZIO
        .attempt(
          readFromString[T](responseBody)
        ) 
    yield response



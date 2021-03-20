package aitu.shyq.api.http

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.RouteConcatenation
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.config.Config
import aitu.shyq.service.EventService
import aitu.shyq.swagger.{SwaggerDocService, SwaggerSite}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

case class HttpServer()(implicit val actorSystem: ActorSystem[Nothing],
                        implicit val executionContext: ExecutionContext,
                        implicit val config: Config,
                        implicit val eventService: EventService)
    extends SwaggerSite
    with RouteConcatenation
    with HttpRoutes {

  private val shutdown = CoordinatedShutdown(actorSystem)

  def start(): Unit = {
    val settings: CorsSettings =
      CorsSettings.defaultSettings.withAllowedMethods(Seq(GET, PUT, POST, HEAD, OPTIONS, DELETE))
    val aggregatedRoutes = {
          cors(settings)(concat(routes, swaggerSiteRoute, new SwaggerDocService().routes))
        }



    Http().newServerAt(config.getString("http-server.interface"), config.getInt("http-server.port")).bind(aggregatedRoutes).onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        actorSystem.log.info("aitu-shyq online at http://{}:{}/", address.getHostString, address.getPort)

        shutdown.addTask(CoordinatedShutdown.PhaseServiceRequestsDone, "http-graceful-terminate") { () =>
          binding.terminate(10.seconds).map { _ =>
            actorSystem.log
              .info("aitu-shyq http://{}:{}/ graceful shutdown completed", address.getHostString, address.getPort)
            Done
          }
        }
      case Failure(ex) =>
        actorSystem.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        actorSystem.terminate()
    }
  }
}

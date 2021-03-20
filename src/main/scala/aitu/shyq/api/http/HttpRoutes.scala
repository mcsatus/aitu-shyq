package aitu.shyq.api.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import aitu.shyq.api.http.route.EventRoutes

import javax.ws.rs.Path

@Path("/api/v1")
trait HttpRoutes
    extends EventRoutes {

  val routes: Route =
      pathPrefix("api" / "v1") {
        concat(
          path("healthcheck") { ctx =>
            complete("ok")(ctx)
          },
          eventRoutes
        )


  }
}

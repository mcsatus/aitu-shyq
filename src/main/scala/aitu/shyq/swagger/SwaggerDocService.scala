package aitu.shyq.swagger

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import com.typesafe.config.Config
import aitu.shyq.api.http.route.EventRoutes

class SwaggerDocService(implicit config: Config) extends SwaggerHttpService {
  override def apiClasses: Set[Class[_]] = Set(classOf[EventRoutes])

  override val schemes: List[String] = List(config.getString("swagger.schemes"))
  override val host: String = config.getString("swagger.host") + "/api/v1"
  override val info: Info = Info(version = "1.0")
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult", "CommandContext")
}

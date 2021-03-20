package aitu.shyq.api.http.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.scalaland.chimney.dsl._
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, ExampleObject, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import aitu.shyq.codec.MainCodec
import aitu.shyq.domain.dto.CreateEventDTO
import aitu.shyq.domain.event.Event
import aitu.shyq.service.EventService
import aitu.shyq.service.EventService.fromParamsToFilterList
import org.joda.time.DateTime

import java.util.UUID
import javax.ws.rs._
import scala.util.{Failure, Success}

@Path("events")
@Tag(name = "Events")
trait EventRoutes extends MainCodec {

  def eventService: EventService

  def eventRoutes: Route =
    pathPrefix("events") {
      concat(
        getAllEvents(),
        getEventByIdRoute(),
        createEventRoute(),
        updateEventRoute(),
        deleteEvent()
      )
    }

  @GET
  @Operation(
    summary = "Get events",
    description = "Get events",
    method = "GET",
    parameters = Array(
      new Parameter(name = "category", in = ParameterIn.QUERY, example = "sport"),
      new Parameter(name = "isActive", in = ParameterIn.QUERY, example = "true"),
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "OK",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[Seq[Event]]),
            mediaType = "application/json",
            examples =
              Array(
                new ExampleObject(
                  name = "Event",
                  value = ""
                )
              )
          )
        )
      ),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  def getAllEvents(): Route = {
    get {
      pathEndOrSingleSlash {
        parameters("category".?, "isActive".as[Boolean].?) { (category, isActive) =>
          onComplete(eventService.findAll(fromParamsToFilterList(category, isActive))) {
            case Success(result) => complete(result)
            case Failure(exception) => complete(exception)
          }
        }
      }
    }
  }

  @GET
  @Operation(
    summary = "Get event by id",
    description = "Returns event by id",
    method = "GET",
    parameters = Array(
      new Parameter(name = "id", in = ParameterIn.PATH, example = "626d2842-b7da-4216-a9c5-f09b7632a90c")
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "OK",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[Event]),
            mediaType = "application/json",
            examples = Array(
              new ExampleObject(
                name = "Event",
                value = ""
              )
            )
          )
        )
      ),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  @Path("/{id}")
  def getEventByIdRoute(): Route = {
    get {
      path(Segment) { id =>
        onComplete(eventService.findById(id)) {
          case Success(result) => complete(result)
          case Failure(exception) => complete(exception)
        }
      }
    }
  }


  @POST
  @Operation(
    summary = "Create event",
    description = "Creates event",
    method = "POST",
    requestBody = new RequestBody(
      content = Array(
        new Content(
          schema = new Schema(implementation = classOf[CreateEventDTO]),
          mediaType = "application/json",
          examples = Array(
            new ExampleObject(
              name = "Event",
              value = ""
            )
          )
        )
      ),
      required = true
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "OK",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[Event]),
            mediaType = "application/json",
            examples = Array(
              new ExampleObject(
                name = "Event",
                value = ""
              )
            )
          )
        )
      ),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  def createEventRoute(): Route = {
    post {
      pathEndOrSingleSlash {
        entity(as[CreateEventDTO]) { createEventDTO =>
          onComplete {
            eventService.create(
              createEventDTO.into[Event]
                .withFieldComputed(_.id, _ => UUID.randomUUID().toString)
                .transform)
          } {
            case Success(result) => complete(result)
            case Failure(exception) => complete(exception)
          }
        }
      }
    }
  }

  @PUT
  @Operation(
    summary = "Update event",
    description = "Updates event",
    method = "PUT",
    parameters = Array(
      new Parameter(name = "id", in = ParameterIn.PATH, example = "626d2842-b7da-4216-a9c5-f09b7632a90c")
    ),
    requestBody = new RequestBody(
      content = Array(
        new Content(
          schema = new Schema(implementation = classOf[CreateEventDTO]),
          mediaType = "application/json",
          examples =
            Array(
              new ExampleObject(
                name = "Event",
                value = ""
              )
            )
        )
      ),
      required = true
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "OK",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[Event]),
            mediaType = "application/json",
            examples =
              Array(
                new ExampleObject(
                  name = "Event",
                  value = ""
                )
              )
          )
        )
      ),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  @Path("/{id}")
  def updateEventRoute(): Route = {
    put {
      path(Segment) { id =>
        entity(as[CreateEventDTO]) { createEventDTO =>
          onComplete {
            eventService.update(
              createEventDTO.into[Event]
                .withFieldComputed(_.id, _ => id)
                .transform
            )
          } {

            case Success(result) => complete(result)
            case Failure(exception) => complete(exception)
          }
        }
      }
    }
  }

  @DELETE
  @Path("/{id}")
  @Operation(
    summary = "Delete event by id",
    description = "Delete event by id",
    method = "DELETE",
    parameters = Array(
      new Parameter(
        name = "id",
        in = ParameterIn.PATH,
        example = "83978149-8e29-4519-9c6a-cdfc0ae1cd59",
        required = true
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "OK",
        content = Array(
          new Content(
            examples = Array(
              new ExampleObject(
                value = ""
              )
            ),
            mediaType = "application/json"
          )
        )
      ),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  def deleteEvent(): Route =
    delete {
      path(Segment) { id =>
        onComplete(eventService.delete(id)) {
          case Success(result) => complete(result)
          case Failure(exception) => complete(exception)
        }
      }
    }
}

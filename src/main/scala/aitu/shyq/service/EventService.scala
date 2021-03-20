package aitu.shyq.service

import akka.actor.typed.ActorSystem
import com.typesafe.config.Config
import io.scalaland.chimney.dsl._
import aitu.shyq.domain.dto.CreateEventDTO
import aitu.shyq.domain.Accepted
import aitu.shyq.domain.event.Event
import aitu.shyq.domain.event.EventFilter.{ByCategory, ByState, EventFilter}
import aitu.shyq.repository.EventRepository
import aitu.shyq.repository.mongo.EventMongoRepository
import org.joda.time.DateTime
import org.mongodb.scala.MongoClient
import org.slf4j.{Logger, LoggerFactory}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object EventService {

  def fromParamsToFilterList(category: Option[String], isActive: Option[Boolean]): Seq[EventFilter] = {
    var filters: Seq[EventFilter] = Seq.empty
    if (category.isDefined) filters = filters :+ ByCategory(category.get)
    if (isActive.isDefined) filters = filters :+ ByState(isActive.get)
    filters
  }

}

class EventService()(implicit val system: ActorSystem[Nothing],
                     val config: Config,
                     val executionContext: ExecutionContext,
                     val mongoClient: MongoClient) {

  val EventRepository: EventRepository = new EventMongoRepository()

  val log: Logger = LoggerFactory.getLogger(getClass.getSimpleName)

  def findAll(filters: Seq[EventFilter]): Future[Seq[Event]] = {
    log.debug(s"findAll() was called")
    EventRepository.findAll(filters)
  }

  def findById(id: String): Future[Event] = {
    log.debug(s"findById was called {id: $id}")
    EventRepository.findById(id).map {
      case Some(event) => event
      case None =>
        log.warn(s"findById() failed to find deposit accrual plan {id: $id}")
        throw new RuntimeException()
    }
  }

  def create(event: Event): Future[Event] = {
    log.debug(s"create() was called {event: $event}")
    EventRepository.create(event)
  }

  def update(event: Event): Future[Event] = {
    log.debug(s"update() was called {event: $event}")
    EventRepository.update(event)
  }

  def delete(id: String): Future[Accepted] = {
    log.debug(s"delete() was called {id: $id}")
    EventRepository.delete(id)
  }

  def countAll(): Future[Long] = {
    EventRepository.count(Seq.empty)
  }


}

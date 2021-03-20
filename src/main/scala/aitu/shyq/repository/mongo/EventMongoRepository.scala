package aitu.shyq.repository.mongo

import akka.actor.typed.ActorSystem
import com.typesafe.config.Config
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import aitu.shyq.codec.MainCodec
import aitu.shyq.domain.Accepted
import aitu.shyq.repository.EventRepository
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import EventMongoRepository._
import aitu.shyq.domain.event.Event
import aitu.shyq.domain.event.EventFilter.{ByCategory, ByState, EventFilter}

object EventMongoRepository {

  private def fromFiltersToBson(filters: Seq[EventFilter]): Bson = {
    if (filters.isEmpty) Document()
    else and(
      filters.map {
        case ByCategory(category) => equal("category", category)
        case ByState(isActive) => equal("isActive", isActive)
      }: _*
    )
  }

}

class EventMongoRepository(implicit val actorSystem: ActorSystem[Nothing],
                           val mongoClient: MongoClient,
                           val config: Config,
                           val executionContext: ExecutionContext) extends EventRepository with MainCodec {

  val database: MongoDatabase = mongoClient.getDatabase(config.getString("db.mongo.database"))
  val collection: MongoCollection[Document] = database.getCollection(config.getString("db.mongo.collection.event"))

  private def toDocument(event: Event): Document = {
    val json: Json = event.asJson
    Document(json.noSpaces)
  }

  private def fromDocumentToEvent(document: Document): Event = {
    parse(document.toJson()).toTry match {
      case Success(json) =>
        json.as[Event].toTry match {
          case Success(d) =>
            d
          case Failure(exception) =>
            throw exception
        }
      case Failure(exception) => throw exception
    }
  }


  def findAll(filters: Seq[EventFilter]): Future[Seq[Event]] = {
    val filterBson = fromFiltersToBson(filters)
    collection.find(filterBson).collect().head().map { documents =>
      documents.map { document =>
        fromDocumentToEvent(document)
      }
    }
  }

  def count(filters: Seq[EventFilter]): Future[Long] = {
    val query = fromFiltersToBson(filters)
    collection.countDocuments(query).head()
  }

  def findById(id: String): Future[Option[Event]] =
    collection.find(equal("id", id)).headOption().map {
      case Some(document) => Some(fromDocumentToEvent(document))
      case None => None
    }

  def create(event: Event): Future[Event] =
    collection.insertOne(toDocument(event)).head().map(_ => event)

  def update(event: Event): Future[Event] =
    collection.replaceOne(
      equal("id", event.id),
      toDocument(event)
    ).head()
      .map { replacedResult =>
        if (replacedResult.wasAcknowledged()) {
          event
        } else {
          throw new RuntimeException(s"Failed to update event with id ${event.id}")
        }
      }

  def delete(id: String): Future[Accepted] =
    collection.deleteOne(equal("id", id))
      .head()
      .map { deleteResult =>
        if (deleteResult.wasAcknowledged()) {
          Accepted(200, Some(s"Event with id $id was deleted"))
        }
        else {
          throw new RuntimeException(s"Failed to delete Event with id $id")
        }
      }
}

package aitu.shyq

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, DispatcherSelector}
import com.typesafe.config.{Config, ConfigFactory}
import aitu.shyq.api.http.HttpServer
import aitu.shyq.codec.MainCodec
import aitu.shyq.service.EventService
import org.mongodb.scala.MongoClient

import scala.concurrent.ExecutionContext

object Boot extends App with MainCodec {

  implicit val config: Config = ConfigFactory.load()

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "aitu-shyq")

  implicit val executionContext: ExecutionContext =
    actorSystem.dispatchers.lookup(DispatcherSelector.fromConfig("akka.dispatchers.boot"))

  implicit val mongoClient: MongoClient = MongoClient(config.getString("db.mongo.connection-string"))

  implicit val eventService: EventService = new EventService()

  HttpServer().start()
}

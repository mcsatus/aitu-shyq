package aitu.shyq.repository

import aitu.shyq.domain.Accepted
import aitu.shyq.domain.event.Event
import aitu.shyq.domain.event.EventFilter.EventFilter

import scala.concurrent.Future

trait EventRepository {

  def findAll(filters: Seq[EventFilter]): Future[Seq[Event]]

  def count(filters: Seq[EventFilter]): Future[Long]

  def findById(id: String): Future[Option[Event]]

  def create(Event: Event): Future[Event]

  def update(Event: Event): Future[Event]

  def delete(id: String): Future[Accepted]

}

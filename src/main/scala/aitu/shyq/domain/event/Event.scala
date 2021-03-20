package aitu.shyq.domain.event

import org.joda.time.DateTime


case class Event(id: String,
                 name: String,
                 category: String,
                 description: String,
                 time: DateTime,
                 groupSize: Int,
                 members: Option[Seq[AituUser]] = None,
                 requests: Option[Seq[AituUser]] = None,
                 chatLink: String,
                 isActive: Boolean
                )

case class AituUser(id: String,
                    name: String,
                    profileLink: String)
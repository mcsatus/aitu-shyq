package aitu.shyq.domain.dto

import org.joda.time.DateTime

case class CreateEventDTO(name: String,
                          category: String,
                          description: String,
                          time: DateTime,
                          groupSize: Int,
                          chatLink: String,
                          isActive: Boolean)



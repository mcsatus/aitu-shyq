package aitu.shyq.domain

import org.joda.time.DateTime

case class Attachment(id: String,
                      link: String,
                      fileName: String,
                      contentType: String,
                      createdAt: DateTime)
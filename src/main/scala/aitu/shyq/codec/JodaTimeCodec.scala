package aitu.shyq.codec

import io.circe.{Decoder, Encoder, HCursor, Json}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, LocalDate, LocalTime}

import scala.util.Try

trait JodaTimeCodec {

  val defaultDateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val dateTimeFormatterWithTime: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  val dateTimeFormatterWithSeconds: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss+SSSS")
  val dateTimeFormatterWithoutMillis: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def dateTimeFormatDecoder(format: DateTimeFormatter): Decoder[DateTime] =
    Decoder[String].emapTry(str => Try(DateTime.parse(str, format)))

  def longDateTimeDecoder: Decoder[DateTime] =
    Decoder[Long].map(long => new DateTime(long))

  implicit val encodeDateTime: Encoder[DateTime] = new Encoder[DateTime] {
    final def apply(a: DateTime): Json = {
      Json.fromLong(a.getMillis)
    }
  }
  implicit val decodeDateTime: Decoder[DateTime] = {
    longDateTimeDecoder
      .or(dateTimeFormatDecoder(defaultDateTimeFormatter))
      .or(dateTimeFormatDecoder(dateFormatter))
      .or(dateTimeFormatDecoder(dateTimeFormatterWithTime))
      .or(dateTimeFormatDecoder(dateTimeFormatterWithSeconds))
      .or(dateTimeFormatDecoder(dateTimeFormatterWithoutMillis))
  }

  implicit val encodeLocalDate: Encoder[LocalDate] = new Encoder[LocalDate] {
    final def apply(a: LocalDate): Json = {
      Json.fromString(a.toString)
    }
  }
  implicit val decodeLocalDate: Decoder[LocalDate] = new Decoder[LocalDate] {
    final def apply(c: HCursor): Decoder.Result[LocalDate] =
      for {
        d <- c.value.as[String]
      } yield {
        LocalDate.parse(d)
      }
  }

  implicit val encodeLocalTime: Encoder[LocalTime] = new Encoder[LocalTime] {
    final def apply(a: LocalTime): Json = {
      Json.fromString(a.toString)
    }
  }
  implicit val decodeLocalTime: Decoder[LocalTime] = new Decoder[LocalTime] {
    final def apply(c: HCursor): Decoder.Result[LocalTime] =
      for {
        d <- c.value.as[String]
      } yield {
        LocalTime.parse(d)
      }
  }

}

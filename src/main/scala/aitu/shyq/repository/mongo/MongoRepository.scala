package aitu.shyq.repository.mongo

import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Sorts.{ascending, descending, orderBy}

object MongoRepository {
  final val DUPLICATED_KEY_ERROR_CODE = 11000
}

trait MongoRepository {

  def sortingFields: Seq[String] = Seq.empty
}


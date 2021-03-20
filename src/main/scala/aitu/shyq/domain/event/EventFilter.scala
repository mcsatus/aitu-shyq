package aitu.shyq.domain.event

object EventFilter {

  trait EventFilter

  case class ByCategory(category: String) extends EventFilter
  case class ByState(isActive: Boolean) extends EventFilter

}

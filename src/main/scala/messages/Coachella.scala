package messages

import actors.Coachella
import akka.actor.Props
import akka.util.Timeout

object Coachella {

  def props(implicit  timeput: Timeout) = Props(new Coachella)

  case class CreateEvent(name: String, tickets: Int)
  case class GetEvent(name: String)
  case object GetEvents
  case class GetTickets(event: String, tickets: Int)
  case class CancelEvent(name: String)

  case class Event(name: String, tickets: Int)
  case class Events(events: Vector[Event])

  sealed trait EventResponse //message response to create an event
  case class EventCreated(event: Event) extends EventResponse
  case object EventExists extends EventResponse // message to an event already exists

}

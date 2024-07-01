package messages

import actors.TicketSeller
import akka.actor.Props

object TicketSeller {

  def props(event: String) = Props(new TicketSeller(event))

  case class Add(tickets: Vector[Ticket])
  case class Buy(tickets: Int)
  case class Ticket(id: Int)
  case class Tickets(event: String, entries: Vector[Ticket] = Vector.empty[Ticket])

  case object GetEvent // a message containing the remaining tickets for an event
  case object Cancel // a message to cancel the event
}

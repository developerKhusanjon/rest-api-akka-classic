package messages

import de.heikoseeberger.akkahttpplayjson._
import messages.Coachella._
import play.api.libs.json._

// message containing the initial number of tickets for the event
case class EventDescription(tickets: Int) {
  require(tickets > 0)
}

// message containing the required number of tickets
case class TicketRequests(tickets: Int) {
  require(tickets > 0)
}

// message containing an error
case class Error(message: String)

trait EventMarshaller extends PlayJsonSupport {
  implicit val eventDescriptionFormat: OFormat[EventDescription] = Json.format[EventDescription]
  implicit val ticketRequests: OFormat[TicketRequests] = Json.format[TicketRequests]
  implicit val errorFormat: OFormat[Error] = Json.format[Error]
  implicit val eventFormat: OFormat[Event] = Json.format[Event]
  implicit val eventsFormat: OFormat[Events] = Json.format[Events]
  implicit val ticketFormat: OFormat[TicketSeller.Ticket] = Json.format[TicketSeller.Ticket]
  implicit val ticketsFormat: OFormat[TicketSeller.Tickets] = Json.format[TicketSeller.Tickets]
}

object EventMarshaller extends EventMarshaller

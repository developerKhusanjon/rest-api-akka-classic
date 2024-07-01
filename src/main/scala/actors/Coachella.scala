package actors

import akka.actor.{Actor, ActorRef}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import messages.Coachella.{CancelEvent, CreateEvent, Event, EventCreated, EventExists, Events, GetEvent, GetEvents, GetTickets}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Coachella(implicit timeout: Timeout) extends Actor {
  import messages.TicketSeller

  def createTicketSeller(name: String): ActorRef = context.actorOf(TicketSeller.props(name), name)

  override def receive: Receive = {
    case CreateEvent(name, tickets) =>
      def create(): Unit = {
        val eventTickets = createTicketSeller(name)
        val newTickets = (1 to tickets).map { ticketId =>
          TicketSeller.Ticket(ticketId)
        }.toVector

        eventTickets ! TicketSeller.Add(newTickets)
        sender() ! EventCreated(Event(name, tickets))
      }
      context.child(name).fold(create())(_ => sender() ! EventExists)

    case GetTickets(event, tickets) =>
      def notFound(): Unit = sender() ! TicketSeller.Tickets(event)
      def buy(child: ActorRef): Unit = child.forward(TicketSeller.Buy(tickets))
      context.child(event).fold(notFound())(buy)
E
  }
}

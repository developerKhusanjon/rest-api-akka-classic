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

    case GetEvent(event) =>
      def notFound() = sender() ! None
      def getEvent(child: ActorRef) = child forward TicketSeller.GetEvent
      context.child(event).fold(notFound())(getEvent)

    case GetEvents =>
      def getEvents = {
        context.children.map { child =>
          self.ask(GetEvent(child.path.name)).mapTo(Option[Event])
        }
      }
      def convertToEvents(f: Future[Iterable[Option[Event]]]): Future[Events] = {
        f.map(_.flatten).map(l => Events(l.toVector))
      }
      pipe(convertToEvents(Future.sequence(getEvents))) to sender()

    case CancelEvent(event) =>
      def notFound(): Unit = sender() ! None
      def cancelEvent(child: ActorRef): Unit = child forward TicketSeller.Cancel
      context.child(event).fold(notFound())(cancelEvent)
  }
}

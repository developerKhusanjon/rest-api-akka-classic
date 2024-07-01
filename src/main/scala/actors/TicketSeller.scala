package actors

import akka.actor.{Actor, PoisonPill}
import messages.Coachella

class TicketSeller(event: String) extends Actor{

  import messages.TicketSeller._

  // list of Tickets
  var tickets = Vector.empty[Ticket]

  override def receive: Receive = {
    //add the new tickets to existing ticket list  when Tickets message received
    case Add(newTickets) => tickets = tickets ++ newTickets
    case Buy(numberOfTickets) =>
      //takes number of tickets from ticket list
      val entries = tickets.take(numberOfTickets)

      if (entries.size >= numberOfTickets) {
        sender() ! Tickets(event, tickets)
        tickets = tickets.drop(numberOfTickets)
      } else sender() ! Tickets(event)

    case GetEvent => sender() ! Some(Coachella.Event(event, tickets.size))
    case Cancel => sender() ! Some(Coachella.Event(event, tickets.size))
      self ! PoisonPill
  }
}

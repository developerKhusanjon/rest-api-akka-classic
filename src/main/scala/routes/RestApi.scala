package routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import messages.Coachella.{CancelEvent, CreateEvent, Event, EventResponse, Events, GetEvent, GetEvents, GetTickets}
import messages.{Coachella, Error, EventDescription, EventMarshaller, TicketSeller}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  implicit val requestTimeout: Timeout = timeout
  implicit def executionContext: ExecutionContextExecutor = system.dispatcher

  override def createCoachella(): ActorRef = system.actorOf(Coachella.props)
}

trait RestRoutes extends CoachellaApi with EventMarshaller {
  val service = "show-tix"
  val version = "v1"

  protected val createEventRoute: Route = {
    pathPrefix(service / version / "events" / Segment) { event =>
      post {
        entity(as[EventDescription]) { ed =>
          onSuccess(createEvent(event, ed.tickets)) {
            case Coachella.EventCreated(event) => complete(Created, event)
            case Coachella.EventExists =>
                val err = Error(s"$event event already exists!")
                complete(BadRequest, err)
          }
        }
      }
    }
  }

  val routes: Route = createEventRoute
}

trait CoachellaApi {

  def createCoachella(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val coachella: ActorRef = createCoachella()

  def createEvent(event: String, numberOfTickets: Int): Future[EventResponse] = {
      coachella.ask(CreateEvent(event, numberOfTickets)).mapTo[EventResponse]
  }

  def getEvents(): Future[Events] = coachella.ask(GetEvents).mapTo[Events]

  def getEvent(event: String): Future[Option[Event]] = coachella.ask(GetEvent).mapTo[Option[Event]]

  def cancelEvent(event: String): Future[Option[Event]] = coachella.ask(CancelEvent).mapTo[Option[Event]]

  def requestTickets(event: String, tickets: Int): Future[TicketSeller.Tickets] =
    coachella.ask(GetTickets(event, tickets)).mapTo[TicketSeller.Tickets]
}

package assets

import akka.actor._
import play.api.libs.json._
import java.util.Calendar

object ActiveClient {
  def props(out: ActorRef, name: String = "Anonymous"): Props =
    Props(new ActiveClient(out, name))
}

object ClientProtocol {
  val TYPE_INFO = "info"
  val TYPE_MSG = "msg"

  case class Hear(msg: String, from: ActorRef)
  case object ConnectionEstablished

  def composeJson(responseType: String, msg: String): String = {
    Json.obj(
      "response" -> responseType,
      TYPE_MSG -> msg,
      "time" -> Calendar.getInstance().getTime().toString())
      .toString()
  }
}

class ActiveClient(out: ActorRef, name: String) extends Actor with ActorLogging {
  import ChatServiceProtocol._
  import ClientProtocol._

  // Get actorRef to chatService-actor
  // 1. send notify message to chatService actor to identify itself
  // 2. receive ActorIdentify message with actorRef of chatService
  // 3. change the state of this actor to become active
  val chatServiceId = self.path.address
  val csAddress = "akka://application/user/chat-service"
  context.actorSelection(csAddress) ! Identify(chatServiceId)

  def receive: Actor.Receive = {
    case ActorIdentity(chatServiceId, Some(chatServiceRef)) =>
      log.debug("ChatService ActorRef collected")
      context.watch(chatServiceRef)
      context.become(active(chatServiceRef))

      chatServiceRef ! Login(self, "default")
    case ActorIdentity(chatServiceId, None) =>
      context.stop(self)

    case _ =>
      out ! composeJson(TYPE_INFO, "No connection to chat server")
  }

  def active(chatService: ActorRef): Actor.Receive = {
    case ConnectionEstablished =>
      out ! composeJson(TYPE_INFO, "Connection Established")

    case Hear(msg, fromUser) =>
      out ! composeJson(TYPE_MSG, msg)

    case msg: String =>
      log.debug("Incoming message from client")

      // get text from Json
      // todo: factor out in helper
      val json: JsValue = Json.parse(msg)
      val reply: JsResult[String]= (json \ TYPE_MSG).validate[String]
      reply match {
        case s: JsSuccess[String] =>
          chatService ! Broadcast(self, s.get, "default")
        case e: JsError =>
          out ! composeJson(TYPE_INFO, "wrong format in json")
      }
  }

  override def postStop() = {
    log.debug("Stopping actor")
  }
}

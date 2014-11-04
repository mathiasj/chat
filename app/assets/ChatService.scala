package assets

import akka.actor.{ActorRef, Props, Actor, ActorLogging}

object ChatService {
  def props: Props = Props(new ChatService())
}

object ChatServiceProtocol {
  case class Login(user: ActorRef, channel: String)
  case class Logout(user: ActorRef)
  case class Say(fromUser: ActorRef, toUser: ActorRef, msg: String)
  case class Broadcast(fromUser: ActorRef, msg: String, channel: String)
}

class ChatService extends Actor with ActorLogging {
  import ChatServiceProtocol._
  import ClientProtocol._

  log.debug("ChatService started at address: {}", self)
  var channels = scala.collection.mutable.Map[ActorRef, String]()

  def receive = {
    case Login(user, channel) =>
      log.debug("Register user with chat service: {}", (user -> channel).toString())
      channels = channels + (user -> channel)
      user ! ConnectionEstablished

    case Say(fromUser, toUser, msg) =>
      toUser ! Hear(msg, fromUser)

    case Broadcast(fromUser, msg, channel) =>
      log.debug("Broadcasting message to users:\n {}", channels.toString)

      // todo: filter channels
      channels.keys.foreach(toUser => {
        self ! Say(fromUser, toUser, msg)
      })
  }
}

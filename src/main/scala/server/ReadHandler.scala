package server

import akka.actor.{ActorLogging, Actor, Props}
import akka.io.Tcp
import akka.util.ByteString

object ReadHandler {
  def props: Props = {
    Props(new ReadHandler)
  }
}

object ReadHandlerProtocol {
}

class ReadHandler extends Actor with ActorLogging {

  def receive = {
    case Tcp.Received(data: ByteString) =>
      val dataStr: String = data.utf8String.trim
      log.debug("INCOMING DATA (actor: {}:\n{}", self.path.name, dataStr)

    case Tcp.PeerClosed =>
      log.debug("Connection closed")
      context stop self
  }
}
package server

import akka.actor.{ActorLogging, Actor, Props}
import play.api.libs.json._
import akka.io.{Tcp, IO}
import java.net.InetSocketAddress
import akka.util.ByteString

object Server {
  def props: Props = {
    Props(new Server())
  }
}

class Server extends Actor with ActorLogging {
  import context.system

  IO(Tcp) ! Tcp.Bind(self, new InetSocketAddress("localhost", 4040))

  def receive = {
    case Tcp.Bound(localAddress: InetSocketAddress) =>
      log.info("Server started at {}", localAddress)

    case Tcp.CommandFailed(_: Tcp.Bind) =>
      log.info("Command failed")
      context stop self

    case Tcp.Connected(remote, local) =>
      log.info("Received connection from ", remote)
      val connection = sender()

      // send incoming data to ReadHandler
      val readHandler = context.actorOf(ReadHandler.props)
      connection ! Tcp.Register(readHandler)

      val response: String = Json.obj(
        "response" -> "info",
        "msg" -> "Connection established"
      ).toString()

      connection ! Tcp.Write((ByteString(response)))
  }
}
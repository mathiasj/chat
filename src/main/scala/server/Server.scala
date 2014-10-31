package server

import akka.actor.{ActorLogging, Actor, Props}
import akka.io.{Tcp, IO}
import java.net.InetSocketAddress

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
      log.debug("Server started at {}", localAddress)

    case Tcp.CommandFailed(_: Tcp.Bind) =>
      log.info("Command failed")
      context stop self

    case Tcp.Connected(remote, local) =>
      log.info("Received connection from ", remote)
      val connection = sender()

      // send incoming data to ReadHandler
      val readHandler = context.actorOf(ReadHandler.props)
      connection ! Tcp.Register(readHandler)
  }
}
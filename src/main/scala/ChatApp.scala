import akka.actor.ActorSystem
import server._

object ChatApp {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem(s"chat-system")
    system.actorOf(Server.props)
  }
}
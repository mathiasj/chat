import akka.actor.Props
import assets.ChatService
import play.api._
import play.libs.Akka

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started");

    // Global actor handling all the chat sessions
    Akka.system.actorOf(Props[ChatService], "chat-service")
  }
}

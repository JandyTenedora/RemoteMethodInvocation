package org.example

import java.rmi.Naming
import java.rmi.server.UnicastRemoteObject

import scalafx.application.JFXApp
import scalafx.scene.control.TextInputDialog

// 1. Make remote interfaces (traits) annoted by @remote
// 2. Make implementation that extends unicastremoteobject and traits
// 3. Need server to bind itself to RMI registry
// 4. Client does a name lookup
// 5. Need an RMI registry running somewhere

@remote trait RemoteClient{
  def name: String
  def message(sender: RemoteClient, text:String): Unit
  def clientUpdate(clients: Seq[RemoteClient]): Unit


}



object RMIChatClient extends UnicastRemoteObject with JFXApp with RemoteClient {

  val dialog = new TextInputDialog("localhost")
  dialog.title = "Server Machine"
  dialog.contentText = "Choose a server to connect to"
  dialog.headerText = "Server Name"
  val (_name,server) = dialog.showAndWait() match {
    case Some(machine) =>
      Naming.lookup(s"rmi://$machine/ChatServer") match {
        case server: RemoteServer =>
          val dialog = new TextInputDialog("")
          dialog.title = "Chat Name"
          dialog.contentText = "What name do you want to go by?"
          dialog.headerText = "User Name"
          dialog.showAndWait() match{
            case Some(name) => (name,server)
            case None => sys.exit(0)
          }
        case _ =>
          println("Error")
          sys.exit(0)
      }
    case None => sys.exit(0)
  }

  def name: String = _name
  def message(sender: RemoteClient, text:String): Unit = ???
  def clientUpdate(clients: Seq[RemoteClient]): Unit = ???
}

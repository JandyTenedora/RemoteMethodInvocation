package org.example

import java.rmi.server.UnicastRemoteObject
import java.rmi.{Naming, RemoteException}
import java.rmi.registry.LocateRegistry

import scalafx.scene.control.TextInputDialog

import scala.collection.mutable

@remote trait RemoteServer{
  def connect(client: RemoteClient): Unit
  def disconnect(client: RemoteClient): Unit
  def getClient: Seq[RemoteClient]
  def publicMessage(client: RemoteClient, text:String): Unit

}

object RMIChatServer extends UnicastRemoteObject with App with RemoteServer {

  LocateRegistry.createRegistry(1099)

  private val clients = mutable.Buffer[RemoteClient]()

  def connect(client: RemoteClient): Unit = {
    clients += client
    sendUpdate

  }
  def disconnect(client: RemoteClient): Unit = {
    clients -= client
    sendUpdate

  }
  def getClient: Seq[RemoteClient] = {
    clients
    /* Some Notes
    Object outputstreams can only pass through things which are serializable
    So with RMI's there are 2 ways to pass things:
    1. Serializables -> get passed by value, takes a copy of the buffer and send the copy to the client, they can't alter our copy
    2. Pass things remotely (each of the remote clients is a subtype of remote, get passed by remote reference). All the contents are remote references.

     */

  }
  def publicMessage(client: RemoteClient, text:String): Unit = { // Want to send things out to various clients
    val message = client.name + " : " + text
    clients.foreach(_.message(client,message))

  }

  private def sendUpdate: Unit = {
    val deadClients = clients.filter { c =>
      try {
        c.clientUpdate(clients)
        false
      } catch {
        case ex: RemoteException => true
      }
    }

    clients --= deadClients

  }
}

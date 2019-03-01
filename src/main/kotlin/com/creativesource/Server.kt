package main.kotlin.com.creativesource

import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.Props
import akka.io.Tcp
import java.net.InetSocketAddress
import akka.io.TcpMessage
import akka.japi.pf.ReceiveBuilder

class Server(private val manager: ActorRef) : AbstractLoggingActor(){

    companion object {
        fun props(manager: ActorRef) = Props.create(Server::class.java, manager)!!
    }

    @Throws(Exception::class)
    override fun preStart() {
        super.preStart()
        log().info("Starting Server Actor")
        val tcp = Tcp.get(context.system).manager()
        tcp.tell(TcpMessage.bind(self, InetSocketAddress("localhost", 9198), 100), self)
    }

    override fun createReceive() =
        ReceiveBuilder()
            .match(Tcp.Bound::class.java) { manager.tell(it, self) }
            .match(Tcp.CommandFailed::class.java) { context.stop(self)}
            .match(Tcp.Connected::class.java) {
                manager.tell(it, self)
                val handler: ActorRef = context.actorOf(Props.create(SocketHandler::class.java))
                sender().tell(TcpMessage.register(handler), self)
            }
            .build()!!
}
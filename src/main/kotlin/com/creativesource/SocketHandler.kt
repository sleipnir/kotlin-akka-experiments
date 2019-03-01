package main.kotlin.com.creativesource

import akka.actor.AbstractLoggingActor
import akka.io.Tcp
import akka.io.Tcp.ConnectionClosed
import akka.io.TcpMessage

class SocketHandler: AbstractLoggingActor() {

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(
                Tcp.Received::class.java
            ) { msg ->
                val data = msg.data()
                val logMsg = data.decodeString("UTF-8")
                log().info("Received -> $logMsg")
                sender.tell(TcpMessage.write(data), self)
            }
            .match(ConnectionClosed::class.java) { context.stop(self) }
            .build()
    }
}
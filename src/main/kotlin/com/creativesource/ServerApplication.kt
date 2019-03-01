package main.kotlin.com.creativesource

import akka.actor.ActorSystem
import akka.io.Tcp

fun main(){
    val system: ActorSystem = ActorSystem.create("ServerActorSystem")
    system.actorOf(Server.props(Tcp.get(system).manager()), "serverActor")
}
package se.lth.immun.diana

import akka.actor._

class Logger extends Actor {

	def receive = {
		case str:String =>
			println(str)
	}
}
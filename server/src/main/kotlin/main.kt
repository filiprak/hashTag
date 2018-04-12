import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import de.jupf.staticlog.Log

import auth.*;

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/login") {
                handle_login()
            }
            get("/demo") {
                Log.info("Got demo request")
                call.respondText("HELLO WORLD!")
            }
        }
    }
    server.start(wait = true)
}

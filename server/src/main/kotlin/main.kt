import server.PubNubServer
import constants.*

fun main(args: Array<String>) {
    val server = PubNubServer(subscribe_key, publish_key)
}

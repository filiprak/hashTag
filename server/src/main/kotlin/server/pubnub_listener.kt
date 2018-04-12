package server;

import com.pubnub.api.*
import com.pubnub.api.enums.*
import com.pubnub.api.callbacks.*
import com.pubnub.api.models.consumer.*
import com.pubnub.api.models.consumer.pubsub.*

import de.jupf.staticlog.Log

class PubNubListener(val return_callback: () -> Unit) : SubscribeCallback() {
    override fun status(pubnub: PubNub, status: PNStatus) {
        val category = status.getCategory()
        Log.info("Got status: $category")
        when (category) {
            PNStatusCategory.PNUnexpectedDisconnectCategory -> {}
            PNStatusCategory.PNConnectedCategory -> {}
            PNStatusCategory.PNReconnectedCategory -> {}
            PNStatusCategory.PNReconnectionAttemptsExhausted -> {}
            PNStatusCategory.PNTimeoutCategory -> {}
            else -> {}
        }
    }

    override fun message(pubnub: PubNub, message: PNMessageResult) {
        Log.info("Message: $message")
        when(message.channel) {
            "LocationSharing" -> {
                Log.info("Location sharing attempt")
                return_callback()
            }
            else -> {
                Log.error( "Wrong channel")
            }
        }
    }

    override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
        Log.info("Presence: $presence")
    }
}


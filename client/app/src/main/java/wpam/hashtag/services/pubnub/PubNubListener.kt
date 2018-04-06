package wpam.hashtag.services.pubnub

import com.pubnub.api.*
import com.pubnub.api.enums.*
import com.pubnub.api.callbacks.*
import com.pubnub.api.models.consumer.*
import com.pubnub.api.models.consumer.pubsub.*

private class PubNubListener(val tag: String) : SubscribeCallback() {
    override fun status(pubnub: PubNub, status: PNStatus) {
        val category = status.getCategory()
        Log.i(tag, "Status: $category")
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
        Log.i(tag, "Message: $message")
        when(message.channel) {
            "LocationSharing" -> {
                val received_location = message.message;//Gson().fromJson(message.message, Location::class.java)
                Log.i(tag, "Received Location: $received_location")
                val publisher = message.publisher
            }
            else -> {
                Log.e(tag, "Wrong channel")
            }
        }
    }

    override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
        Log.i(tag, "Presence: $presence")
    }
}

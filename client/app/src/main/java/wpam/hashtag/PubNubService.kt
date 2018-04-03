package wpam.hashtag

import java.util.Arrays

import android.app.*
import android.util.*
import android.content.*

import com.pubnub.api.*
import com.pubnub.api.enums.*
import com.pubnub.api.callbacks.*
import com.pubnub.api.models.consumer.*
import com.pubnub.api.models.consumer.pubsub.*

import com.google.gson.*

class PubNubService : IntentService {
    private lateinit var pubnub: PubNub;
    private lateinit var tag: String;

    constructor() : super("PubNubService") {
        Log.d(tag, "constructor")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(tag, "onStartCommand")
        tag = GetMetaData(this, "log_tag") ?: ""
        setupPubNub()
        pubnub.addListener(PubNubListener(tag))
        pubnub.subscribe()
            .channels(Arrays.asList("LocationSharing"))
            .execute()
        Log.i(tag, "Connection success")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupPubNub() {
        val config = PNConfiguration()
        config.setSubscribeKey(GetMetaData(this, "com.pubnub.subscribe.API_KEY"))
        config.setPublishKey(GetMetaData(this, "com.pubnub.publish.API_KEY"))
        pubnub = PubNub(config)
    }

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

    override fun onHandleIntent(intent: Intent) {
        pubnub.publish()
            .message("dupa")
            .channel("LocationSharing")
            .async(object: PNCallback<PNPublishResult>() {
                override fun onResponse(result: PNPublishResult, status: PNStatus) {
                    Log.i(tag, "Result of location sharing: $result, status: $status")
                }
            })
    }
}

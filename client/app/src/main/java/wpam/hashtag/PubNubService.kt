package wpam.hashtag

import java.util.Arrays

import android.app.*
import android.content.*
import android.os.*
import android.util.*

import com.pubnub.api.*
import com.pubnub.api.enums.*
import com.pubnub.api.callbacks.*
import com.pubnub.api.models.consumer.*
import com.pubnub.api.models.consumer.pubsub.*

import com.google.gson.*

class PubNubService : IntentService {
    private lateinit var pubnub: PubNub;
    private lateinit var tag: String;
    private val binder = PubNubBinder(this);
    private var messenger: Messenger? = null;

    constructor() : super("PubNubService") {
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        tag = GetMetaData(this, "log_tag") ?: ""
        Log.d(tag, "onStartCommand")
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

    private fun showNotification() {
        val notificationIntent = Intent(this, PubNubService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = Notification.Builder(this, getText(R.string.notification_channel_id).toString())
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.hash)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.notification_ticker_text))
            .build();

        startForeground(R.integer.pub_nub_notification_id, notification)
    }

    override fun onHandleIntent(intent: Intent) {
        intent.getExtras()?.get("")
        pubnub.publish()
            .message("dupa")
            .channel("LocationSharing")
            .async(object: PNCallback<PNPublishResult>() {
                override fun onResponse(result: PNPublishResult, status: PNStatus) {
                    Log.i(tag, "Result of location sharing: $result, status: $status")
                }
            })
    }

    override fun onBind(intent: Intent): IBinder {
        messenger = intent.getExtras()?.get("messenger") as Messenger
        return binder
    }

    public class PubNubBinder(private val service: PubNubService): Binder() {
        public fun getService(): PubNubService {
            return service
        }
    }
}

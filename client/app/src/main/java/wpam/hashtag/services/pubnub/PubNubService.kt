package wpam.hashtag.services.pubnub

import java.util.Arrays

import android.app.*
import android.content.*
import android.content.res.Resources
import android.os.*
import android.util.*

import com.pubnub.api.*
import com.pubnub.api.enums.*
import com.pubnub.api.callbacks.*
import com.pubnub.api.models.consumer.*
import com.pubnub.api.models.consumer.pubsub.*

import wpam.hashtag.R
import wpam.hashtag.services.location.*

class PubNubService : IntentService {
    private lateinit var pubnub: PubNub
    private var tag = ""
    private val binder = PubNubBinder(this)
    private var messenger: Messenger? = null
    private var locationServiceBinder: LocationService? = null
    private var listener: PubNubListener? = null

    public val locationHandler = object: Handler() {
        override fun handleMessage(message: Message) {
            Log.e(tag, "Got message: $message")
            pubnub.publish()
                .message(message.obj)
                .channel("LocationSharing")
                .async(object: PNCallback<PNPublishResult>() {
                    override fun onResponse(result: PNPublishResult, status: PNStatus) {
                        Log.i(tag, "Result of location sharing")
                    }
            })
        }
    }

    public val locationConnection = object: ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            locationServiceBinder = (binder as LocationService.LocationBinder).getService();
            Log.d(tag,"Location service connected");
        }

        override fun onServiceDisconnected(className: ComponentName) {
            locationServiceBinder = null
            Log.d(tag,"Location service disconnected");
        }
    }

    constructor() : super("PubNubService") {
    }

    override fun onCreate() {
        super.onCreate()
        tag = getString(R.string.log_tag)
        Log.d(tag, "PubNubService: onCreate")
        setupPubNub()
        listener = PubNubListener(tag)
        pubnub.addListener(listener)
        pubnub.subscribe()
            .channels(Arrays.asList("LocationSharing"))
            .execute()
        Log.i(tag, "Connection success")
    }

    private fun setupPubNub() {
        val config = PNConfiguration()
        config.setPublishKey(getString(R.string.pubnub_publish_api_key))
        config.setSubscribeKey(getString(R.string.pubnub_subscribe_api_key))
        pubnub = PubNub(config)
    }

    private fun showNotification() {
        val notificationIntent = Intent(this, PubNubService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = Notification.Builder(this)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.push_logo)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.notification_ticker_text))
            .build();

        startForeground(R.integer.pub_nub_notification_id, notification)
    }

    override fun onHandleIntent(intent: Intent) {
        Log.d(tag, "PubNubService: onHandleIntent")
        showNotification()
        when(intent.getStringExtra("request")) {
            "share_location" -> {
                val location_intent = Intent(this, LocationService::class.java)
                location_intent.putExtra("messenger", Messenger(locationHandler))
                bindService(location_intent, locationConnection, Context.BIND_AUTO_CREATE)
            }
            else -> {
                Log.d(tag, "Unknown request")
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(tag, "PubNubService: onBind")
        messenger = intent.getExtras()?.get("messenger") as Messenger
        listener?.messenger = messenger
        return binder
    }

    public class PubNubBinder(private val service: PubNubService): Binder() {
        public fun getService(): PubNubService {
            return service
        }
    }

}

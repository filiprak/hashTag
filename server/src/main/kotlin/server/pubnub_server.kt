package server;

import java.util.Arrays

import com.pubnub.api.*
import com.pubnub.api.enums.*
import com.pubnub.api.callbacks.*
import com.pubnub.api.models.consumer.*
import com.pubnub.api.models.consumer.pubsub.*

import de.jupf.staticlog.Log

public class PubNubServer {
    val pubnub: PubNub

    constructor(subscribe_key: String, publish_key: String) {
        pubnub = PubNub(getConfig(subscribe_key, publish_key))
        setubListener()
    }

    private fun getConfig(subscribe_key: String, publish_key: String): PNConfiguration {
        val config = PNConfiguration()
        config.setPublishKey(publish_key)
        config.setSubscribeKey(subscribe_key)
        return config
    }

    private fun setubListener() {
        pubnub.addListener(PubNubListener(::onPubNubMessage))
        pubnub.subscribe()
            .channels(Arrays.asList("LocationSharing"))
            .execute()
    }

    private fun onPubNubMessage() {
        Log.info("onPubNubMessage")
    }
}

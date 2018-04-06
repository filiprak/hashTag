package wpam.hashtag.services.pubnub

import android.util.*
import android.content.res.Resources

import wpam.hashtag.R

interface PubNubHandler {
    companion object {
        public val tag = Resources.getString(R.string.log_tag)

        public fun make(request: String): PubNubHandler {
            when(request) {
                else -> return InvalidRequestHandler(request)
            }
        }
    }

    abstract fun handle_request()
}

class InvalidRequestHandler(val request: String): PubNubHandler {
    override fun handle_request() {
        Log.e(PubNubHandler.tag, "Invalid request: $request")
    }
}

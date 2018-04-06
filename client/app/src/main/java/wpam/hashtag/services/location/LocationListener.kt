package wpam.hashtag.services.location

import android.location.*
import android.content.*
import android.util.*
import android.app.*
import android.os.*

class LocationShareListener: LocationListener {
    var lastLocation: Location? = null
    var tag: String = ""
    var messenger: Messenger? = null

    enum class MessageType {
        LOCATION_CHANGED;
    }

    constructor(provider: String) {
        this.lastLocation = Location(provider)
    }

    override fun onLocationChanged(location: Location) {
        Log.i(tag, "Location changed to: $location")
        messenger?.send(Message.obtain(null, MessageType.LOCATION_CHANGED.ordinal, location))
        if (messenger == null) {
            Log.e(tag, "no messenger")
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.i(tag, "Provider enabled: $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Log.i(tag, "Provider disabled: $provider")
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.i(tag, "Status changed: $provider -> $status")
    }
}

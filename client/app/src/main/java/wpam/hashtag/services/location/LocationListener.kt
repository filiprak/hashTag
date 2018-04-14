package wpam.hashtag.services.location

import android.location.*
import android.content.*
import android.util.*
import android.app.*
import android.os.*
import wpam.hashtag.HashTagApplication
import wpam.hashtag.HashTagLocation
import wpam.hashtag.LocationToHashTagLocation

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

    override fun onLocationChanged(loc: Location) {
        Log.i(tag, "Location changed to: $loc")
        val htLocation = LocationToHashTagLocation(loc, HashTagApplication.UID)
        messenger?.send(Message.obtain(null, MessageType.LOCATION_CHANGED.ordinal, htLocation))
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

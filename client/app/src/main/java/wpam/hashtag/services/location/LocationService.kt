package wpam.hashtag.services.location

import android.os.*
import android.app.*
import android.util.*
import android.content.*
import android.location.*

import wpam.hashtag.services.pubnub.*

import wpam.hashtag.R

class LocationService: IntentService {
    private var tag = ""
    private var locationManager: LocationManager? = null;
    private val locationProviders = listOf(
        Pair(LocationManager.GPS_PROVIDER, LocationShareListener(LocationManager.GPS_PROVIDER)),
        Pair(LocationManager.NETWORK_PROVIDER, LocationShareListener(LocationManager.NETWORK_PROVIDER)))
    private val binder = LocationBinder(this)

    companion object {
        private val locationInterval : Long = 1000;
        private val locationDistance = 10f;
    }

    constructor() : super("LocationService") {
    }

    override fun onCreate() {
        super.onCreate()
        tag = getString(R.string.log_tag)
        Log.i(tag, "LocationService: onCreate")
        initializeLocationManager()
        for (locationProvider in locationProviders) {
            try {
                locationProvider.second.tag = tag
                locationManager!!.requestLocationUpdates(
                    locationProvider.first,
                    locationInterval,
                    locationDistance,
                    locationProvider.second)
            } catch (exception: java.lang.SecurityException) {
                Log.i(tag, "Fail to request location update, ignore: $exception")
            } catch (exception: IllegalArgumentException) {
                Log.d(tag, "Network provider does not exist, $exception")
            }
        }
    }

    private fun initializeLocationManager() {
        if (locationManager == null)
            locationManager = getApplicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onDestroy() {
        Log.e(tag, "onDestroy")
        if (locationManager != null) {
            for (locationProvider in locationProviders) {
                try {
                    locationManager!!.removeUpdates(locationProvider.second)
                } catch (exception: Exception) {
                    Log.i(tag, "Fail to remove location listners, ignore: $exception")
                }
            }
        }
    }

    override fun onHandleIntent(intent: Intent) {
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(tag, "LocationService: onBind")
        val messenger = intent.getExtras()?.get("messenger") as Messenger
        for (locationProvider in locationProviders)
            locationProvider.second.messenger = messenger
        return binder
    }

    public class LocationBinder(private val service: LocationService): Binder() {
        public fun getService(): LocationService {
            return service
        }
    }
}

package wpam.hashtag.activities

import android.content.*
import android.content.res.Resources
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.*
import android.util.Log
import android.view.KeyEvent

import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions

import wpam.hashtag.GetMetaData
import wpam.hashtag.HashTagLocation
import wpam.hashtag.R

import wpam.hashtag.services.pubnub.PubNubService


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    private val MY_PERMISSIONS_REQUEST_CHECK_SETTINGS = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var mCurrentLocation: Location? = null
    private lateinit var mMap: GoogleMap

    private var tag = ""

    private var pubNubServiceBinder: PubNubService? = null

    public val pubNubConnection = object: ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            pubNubServiceBinder = (binder as PubNubService.PubNubBinder).getService();
            Log.d(tag,"connected");
        }

        override fun onServiceDisconnected(className: ComponentName) {
            pubNubServiceBinder = null
            Log.d(tag,"disconnected");
        }
    }

    public val pubNubHandler = object: Handler() {
        override fun handleMessage(message: Message) {
            Log.i(tag, "MapsActivity got message: $message")
        }
    }


    public fun doBindService() {
        Log.i(tag, "doBindService")
        val intent = Intent(this, PubNubService::class.java)
        intent.putExtra("messenger", Messenger(pubNubHandler))
        bindService(intent, pubNubConnection, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tag = GetMetaData(this, "log_tag") ?: tag
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        doBindService()
        shareLocation()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
            && keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
                Log.d(tag, "onKeyDown Called")
                val stopIntent = Intent(this, PubNubService::class.java)
                stopIntent.putExtra("request", "stop")
                startService(stopIntent)
                unbindService(pubNubConnection)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun shareLocation() {
        val shareLocationIntent = Intent(this, PubNubService::class.java)
        shareLocationIntent.putExtra("request", "share_location")
        startService(shareLocationIntent)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle(mMap, R.raw.map_style_dblue)
    }

    fun setMapStyle(googleMap: GoogleMap, style_id: Int) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, style_id))

            if (!success) {
                Log.e(tag, "Style parsing failed.")
            }

        } catch (e: Resources.NotFoundException) {
            Log.e(tag, "Can't find style. Error: ", e);
        }
    }
}

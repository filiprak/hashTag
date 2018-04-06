package wpam.hashtag

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task

import kotlinx.android.synthetic.main.activity_maps.*

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
        val intent = Intent(this, PubNubService::class.java);
        intent.putExtra("messenger", Messenger(pubNubHandler))
        bindService(intent, pubNubConnection, Context.BIND_AUTO_CREATE);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tag = GetMetaData(this,"log_tag")?: tag
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        doBindService()
        shareLocation()
        Log.d(tag, "service set")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                    if (mCurrentLocation != null) {
                        val popts = PolylineOptions().apply {
                            color(Color.RED)
                            width(20f)
                            visible(true)
                            add(LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude))
                            add(LatLng(location.latitude, location.longitude))
                        }
                        mCurrentLocation = location
                        mMap.addPolyline(popts)
                    }
                    //Toast.makeText(this@MapsActivity, "New location:\n" + location, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private fun shareLocation() {
        val shareLocationIntent = Intent(this, PubNubService::class.java)
        shareLocationIntent.putExtra("request", "share_location")
        startService(shareLocationIntent)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(map.view!!, "Location services permissions required",
                    Snackbar.LENGTH_INDEFINITE).setAction("Grant", { view ->
                        // Request the permission
                        ActivityCompat.requestPermissions(this@MapsActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }).show()

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        } else {
            Log.i("PERMISSIONS", "requestLocationPermission(): permissions ACCESS_FINE_LOCATION already granted")
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Log.i("PERMISSIONS", "permission ACCESS_FINE_LOCATION was granted")
                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                } else {
                    Log.w("PERMISSIONS", "permission ACCESS_FINE_LOCATION was not granted")
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this@MapsActivity, "Permission ACCESS_FINE_LOCATION was not granted",
                            Toast.LENGTH_SHORT).show();
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
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

    @SuppressLint("RestrictedApi")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle(mMap, R.raw.map_style_dblue)
        requestLocationPermission()

        // get location test parameters
        val dbuilder = AlertDialog.Builder(this)
        dbuilder.setTitle("Location Tracking Settings")
        val dview = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)
        val intervValEditText = dview.findViewById<EditText>(R.id.intervVal)
        val fintervValEditText = dview.findViewById<EditText>(R.id.fintervVal)

        dbuilder.setView(dview)
        dbuilder.setPositiveButton("OK") { dialog, p1 ->
            val locationRequest = LocationRequest().apply {
                interval = intervValEditText.text.toString().toLong()
                fastestInterval = fintervValEditText.text.toString().toLong()
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            initializeMyLocation(locationRequest)
            Log.i("locationRequest", "locationRequest $locationRequest")
        }
        dbuilder.setNegativeButton("Cancel") { dialog, p1 ->
            dialog.dismiss()
        }
        dbuilder.show()
    }

    private fun initializeMyLocation(locationRequest: LocationRequest) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return

        // ACCESS_FINE_LOCATION is granted
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener({
            Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            return@setOnMyLocationButtonClickListener false
        })
        mMap.setOnMyLocationClickListener({ location ->
            Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show()
        })
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        mCurrentLocation = location
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                    }
                }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this@MapsActivity,
                            MY_PERMISSIONS_REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
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

package com.devrachit.sample

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.devrachit.sample.ui.theme.SampleTheme
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

const val CUSTOM_INTENT_USER_ACTION = "USER-ACTIVITY-DETECTION-INTENT-ACTION"
const val CUSTOM_REQUEST_CODE_USER_ACTION = 1000

const val CUSTOM_INTENT_GEOFENCE = "GEOFENCE-TRANSITION-INTENT-ACTION"
const val CUSTOM_REQUEST_CODE_GEOFENCE = 1001
class MainActivity : ComponentActivity() {
        private lateinit var geofencingClient: GeofencingClient
        private lateinit var geofenceManager: GeofenceManager
        var geofenceList = mutableListOf<Geofence>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceManager = GeofenceManager(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                100
            )
        } else {
            setupGeofences()
        }

        enableEdgeToEdge()
        setContent{
            SampleTheme{
                Scaffold(modifier=Modifier.fillMaxSize()){innerPadding ->
                    Log.d("mainActivity", innerPadding.toString())
                }
            }
        }
    }
    private fun setupGeofences() {
        geofenceManager.addGeofence("Work", 28.674436729054637, 77.50457707984442,  5f)
        geofenceManager.addGeofence("Home", 28.674336715297404, 77.50458043260561, 5f)
        geofenceManager.addGeofence("Home", 77.50458043260561, 28.674336715297404, 5f)
        geofenceManager.addGeofence("ground",  77.50457707984442, 28.674436729054637, 5f)
        geofenceManager.registerGeofence()
    }


    fun registerGeofences(context:Context) {
        val geofencingRequest = getGeofencingRequest()
        val geofencePendingIntent: PendingIntent = getGeofencePendingIntent(context)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()
            return
        }

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Toast.makeText(context, "Geofences added", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(context, "Failed to add geofences", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun addGeofence(requestId: String, location: LatLng, radius: Float = 100f) {
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(location.latitude, location.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        )
    }
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    private fun getGeofencePendingIntent(context:Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

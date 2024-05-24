package com.devrachit.sample

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import android.Manifest


class GeofenceManager(private val context: Context) {
    private val TAG = "GeofenceManager"
    private val client = LocationServices.getGeofencingClient(context)
    val geofenceList = mutableListOf<Geofence>()

    private val geofencingPendingIntent : PendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            CUSTOM_REQUEST_CODE_GEOFENCE,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

//    fun addGeofence(
//        key: String,
//        location: Location,
//        radiusInMeters: Float = 100.0f,
//        expirationTimeInMillis: Long = 30 * 60 * 1000,
//    ) {
//        geofenceList[key] = createGeofence(key, location, radiusInMeters, expirationTimeInMillis)
//    }
    fun addGeofence(requestId: String, latitude: Double, longitude: Double, radius: Float = 2f) {
        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(latitude, longitude, 10f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        geofenceList.add(geofence)
    }


    fun removeGeofence(key: String) {
        geofenceList.removeIf { it.requestId == key }
    }


    fun registerGeofence() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()
            return
        }
        client.addGeofences(createGeofencingRequest(), geofencingPendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "registerGeofence: SUCCESS")
            }.addOnFailureListener { exception ->
                Log.d(TAG, "registerGeofence: Failure\n$exception")
            }
    }

    suspend fun deregisterGeofence() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
        geofenceList.clear()
    }

    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT)
//            setInitialTrigger(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
            addGeofences(geofenceList)
        }.build()
    }

    private fun createGeofence(
        key: String,
        location: Location,
        radiusInMeters: Float,
        expirationTimeInMillis: Long,
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, radiusInMeters)
            .setExpirationDuration(expirationTimeInMillis)
            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
            .build()
    }

}
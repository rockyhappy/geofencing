package com.devrachit.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofenceStatusCodes

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofenceBroadcastReceiver", errorMessage)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i("GeofenceBroadcastReceiver", "Geofence entered!")
            // Handle geofence enter
            Toast.makeText(context, "Geofence entered!", Toast.LENGTH_LONG).show()
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i("GeofenceBroadcastReceiver", "Geofence exited!")
            Toast.makeText(context, "Geofence exited!", Toast.LENGTH_LONG).show()
            // Handle geofence exit
        }
    }
}


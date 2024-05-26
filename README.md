# Android Geofencing with Kotlin and Jetpack Compose

This project demonstrates the implementation of geofencing in an Android application using Kotlin and Jetpack Compose.

## Features

- Create and manage geofences
- Receive notifications when entering or exiting a geofence
- Use Jetpack Compose for a modern, declarative UI

## Technologies Used

- Kotlin
- Jetpack Compose
- Android Jetpack (LiveData, ViewModel)
- Google Play Services Location

## Getting Started

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or higher
- Minimum SDK version: 21
- Target SDK version: 31

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/GeofencingApp.git
    ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Build and run the project on an Android device or emulator with Google Play Services.

### Usage

1. Launch the app.
2. Add a geofence by entering the latitude, longitude, and radius.
3. Save the geofence.
4. Move into and out of the geofence area to receive notifications.

## Code Overview

### Main Files

- `MainActivity.kt`: Entry point of the app, contains the main UI using Jetpack Compose.
- `GeofenceViewModel.kt`: Handles the geofence logic and interacts with the repository.
- `GeofenceRepository.kt`: Manages geofence data and communicates with the Google Geofencing API.
- `GeofenceReceiver.kt`: BroadcastReceiver to handle geofence transitions.
- `GeofenceUtils.kt`: Utility functions for geofence operations.

### Key Components

- **Jetpack Compose**: Used for building the UI in a declarative way.
- **Google Play Services Location**: Provides APIs to create and monitor geofences.

### Example Code

#### Creating a Geofence

```kotlin
fun createGeofence(latitude: Double, longitude: Double, radius: Float, geofenceId: String): Geofence {
    return Geofence.Builder()
        .setRequestId(geofenceId)
        .setCircularRegion(latitude, longitude, radius)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        .build()
}

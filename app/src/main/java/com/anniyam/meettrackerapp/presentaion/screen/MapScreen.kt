package com.anniyam.meettracker.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.NavController
 import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.maps.android.compose.*
 @Composable
fun MapScreen(
    navController: NavController,
    meetingId: String,
    mobilenumber: String
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val database = FirebaseDatabase.getInstance().getReference("meetings")

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }


     var meetingLocation by remember { mutableStateOf<LatLng?>(null) }


     var userDetails by remember {
         mutableStateOf<Map<String, Pair<String, LatLng>>>(emptyMap())
     }

    // Permission launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Ask permission
    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Start location updates (every 2 sec)
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            startLocationUpdates(fusedLocationClient) { latLng ->
                userLocation = latLng

                val locationData = mapOf(
                    "latitude" to latLng.latitude,
                    "longitude" to latLng.longitude,
                    "timestamp" to System.currentTimeMillis()
                )

                database.child(meetingId).child("users").child(mobilenumber)
                    .child("location")
                    .setValue(locationData)
                    .addOnFailureListener {
                        Log.e("MapScreen", "Failed to update location: ${it.message}")
                    }
            }
        }
    }

     // 🔥 Listen meeting + users (FIXED)
     DisposableEffect(Unit) {

         val ref = database.child(meetingId)

         val listener = object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {

                 // ✅ Meeting location
                 val mLat = snapshot.child("Location").child("latitude")
                     .getValue(String::class.java)?.toDoubleOrNull()

                 val mLng = snapshot.child("Location").child("longitude")
                     .getValue(String::class.java)?.toDoubleOrNull()

                 if (mLat != null && mLng != null) {
                     meetingLocation = LatLng(mLat, mLng)
                 }

                 // ✅ Users
                 val updatedMap = mutableMapOf<String, Pair<String, LatLng>>()

                 val usersSnapshot = snapshot.child("users")

                 for (child in usersSnapshot.children) {

                     val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                     val mobile = child.child("mobile").getValue(String::class.java) ?: ""

                     val lat = child.child("location").child("latitude")
                         .getValue(Double::class.java)

                     val lng = child.child("location").child("longitude")
                         .getValue(Double::class.java)

                     if (lat != null && lng != null) {
                         updatedMap[child.key ?: "Unknown"] =
                             Pair("$name ($mobile)", LatLng(lat, lng))
                     }
                 }

                 userDetails = updatedMap
             }

             override fun onCancelled(error: DatabaseError) {
                 Log.e("MapScreen", "Firebase error", error.toException())
             }
         }

         ref.addValueEventListener(listener)

         onDispose {
             ref.removeEventListener(listener)
         }
     }

     val cameraPositionState = rememberCameraPositionState {
         position = CameraPosition.fromLatLngZoom(
             LatLng(13.0827, 80.2707),
             10f
         )
     }

     // Move camera once
     var isCameraMoved by remember { mutableStateOf(false) }

     LaunchedEffect(userDetails) {
         if (!isCameraMoved) {
             userDetails.values.firstOrNull()?.let {
                 cameraPositionState.position =
                     CameraPosition.fromLatLngZoom(it.second, 15f)
                 isCameraMoved = true
             }
         }
     }

     GoogleMap(
         modifier = Modifier.fillMaxSize(),
         cameraPositionState = cameraPositionState,
         properties = MapProperties(
             isMyLocationEnabled = hasLocationPermission
         ),
         uiSettings = MapUiSettings(
             myLocationButtonEnabled = true,
             zoomControlsEnabled = true
         )
     ) {

         val carIcon = remember {
             bitmapDescriptorFromDrawable(
                 context,
                 com.anniyam.meettrackerapp.R.drawable.ic_person
             )
         }


         // 📍 Meeting Marker
         meetingLocation?.let {
             val markerState = rememberMarkerState(position = it)

             Marker(
                 state = markerState,
                 title = "Meeting Location",
                 snippet = "Meeting ID: $meetingId",
                 icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
             )
             LaunchedEffect(Unit) {
                 markerState.showInfoWindow()
             }
         }



         // 👤 User Markers
         userDetails.forEach { (userId, data) ->
             val latLng = data.second
             val title = data.first

             // Use key(userId) to ensure Compose tracks each specific user correctly
             key(userId) {
                 // To make markers move, we update the position of the MarkerState
                 // whenever the latLng from Firebase changes.
                 val markerState = rememberMarkerState(position = latLng)

                 // This block forces the marker to move when new coordinates arrive
                 LaunchedEffect(latLng) {
                     markerState.position = latLng
                 }

                 Marker(
                     state = markerState,
                     title = title,
                     snippet = "Last seen: ${System.currentTimeMillis()}", // Optional: shows data is fresh
                     icon = carIcon, // Using your custom icon logic
                     anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.5f), // Center car icon
                     flat = true // Keeps car flat on the map
                 )

                 // Optional: Show info window only for the current user or once on load
                 LaunchedEffect(Unit) {
                     markerState.showInfoWindow()
                 }
             }
         }
     }
}


// 🔥 Continuous location updates
@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (LatLng) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        2000 // 2 seconds
    ).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                onLocationUpdate(LatLng(it.latitude, it.longitude))
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}
fun bitmapDescriptorFromDrawable(
    context: Context,
    drawableId: Int
): BitmapDescriptor {

    val drawable = ContextCompat.getDrawable(context, drawableId)!!

    // 👇 CONTROL SIZE HERE (change this)
    val width = 50   // try 30, 40, 50
    val height = 50

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
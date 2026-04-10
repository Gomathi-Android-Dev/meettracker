package com.anniyam.meettrackerapp.presentaion.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPickerScreen(navController: NavController) {
    // Default center (can be user's current location)
    val defaultLocation = LatLng(13.0827, 80.2707)
    var pickedLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                pickedLocation = latLng
            }
        ) {
            pickedLocation?.let {
                Marker(state = MarkerState(position = it), title = "Meeting Point")
            }
        }

        // Confirm Button
        if (pickedLocation != null) {
            Button(
                onClick = {
                    // Send result back to the previous screen
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "picked_location",
                        "${pickedLocation!!.latitude},${pickedLocation!!.longitude}"
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text("Confirm Location")
            }
        }
    }
}
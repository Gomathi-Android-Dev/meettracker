package com.anniyam.meettrackerapp.presentaion.screen

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingSchduleScreen(navController: NavController, onMeetingCreated: (String, String, String, String) -> Unit){
    // State for form fields
    var meetingType by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("No location selected") }

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val meetingTypes = listOf("Business", "Personal", "Project Review", "Client Meet")
    var locationLatLng by remember { mutableStateOf<String?>(null) } // Store actual coordinates
    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry) {
        val result = navBackStackEntry?.savedStateHandle?.get<String>("picked_location")
        if (result != null) {
            locationLatLng = result
            selectedLocation = "Location Set: $result"
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Schedule Meeting") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Meeting Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 1. Meeting Type (Exposed Dropdown Menu)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = meetingType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Meeting Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                     onDismissRequest = { expanded = false }
                ) {
                    meetingTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                meetingType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // 2. Reason for Meeting
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason") },
                placeholder = { Text("Enter meeting purpose...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // 3. Location Picker Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Location", fontWeight = FontWeight.SemiBold)
                        Text(selectedLocation, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = {
                        navController.navigate("map_picker_screen")
                    }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Pick Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Create Button
            Button(
                onClick = {

                    val generatedId = generateMeetingId()
                    onMeetingCreated(generatedId,meetingType, reason, locationLatLng ?: "0.0,0.0")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = meetingType.isNotEmpty() && reason.isNotEmpty(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Create Meeting", fontSize = 16.sp)
            }
        }
    }
}
fun generateMeetingId(): String {
    val letters = ('A'..'Z')
    val digits = ('0'..'9')

    val randomPart = buildString {
        repeat(1) { append(letters.random()) }   // 1 letter
        repeat(3) { append(digits.random()) }    // 3 digits
    }

    return "MEET-$randomPart"
}

@Preview
@Composable
fun MeetingSchduleScreenPreview() {
  //  MeetingSchduleScreen()
}
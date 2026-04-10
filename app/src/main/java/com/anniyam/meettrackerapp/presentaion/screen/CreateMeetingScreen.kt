package com.anniyam.meettracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun CreateMeetingScreen(
    navController: NavController,
    onJoinMeeting: (String, String, String) -> Unit
) {
    // State for the meeting ID text field
    var meetingId by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    var mobileNumber by remember { mutableStateOf("") }
    var signinFlag by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Start a New Meeting",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Meeting ID Input Field
        OutlinedTextField(
            value = meetingId,
            onValueChange = { meetingId = it },
            label = { Text("Enter Meeting ID") },
            placeholder = { Text("e.g. ABC-123-XYZ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                // Button to generate a random ID
                IconButton(onClick = {
                    meetingId = (100000..999999).random().toString()
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Generate ID")
                }
            }
        )
        if (signinFlag) {
            // Meeting ID Input Field
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Enter User Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,


                )
            // Meeting ID Input Field
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Enter Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

                )
        }

        Spacer(modifier = Modifier.height(24.dp))
        // SECONDARY BUTTON: Join / Sign in with ID
        FilledTonalButton(
            onClick = {

                if (!signinFlag) {
                    signinFlag = true
                } else {
                    // Subsequence clicks: validation and navigation
                    if (userName.isNotBlank() && mobileNumber.isNotBlank()) {
                        onJoinMeeting(meetingId, userName, mobileNumber)
                    }
                }
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = meetingId.isNotBlank(),
            shape = MaterialTheme.shapes.medium
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Join with Meeting ID",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //        // Create Button
        Button(
            onClick = {
                navController.navigate("create_meeting")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
             shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Meeting",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

    }
}

@Preview
@Composable
fun CreateMeetingScreenPreview() {
 }
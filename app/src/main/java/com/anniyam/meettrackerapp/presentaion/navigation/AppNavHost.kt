package com.anniyam.chatapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.anniyam.meettracker.ui.screen.CreateMeetingScreen
import com.anniyam.meettracker.ui.screen.MapScreen
import com.anniyam.meettracker.ui.screen.MeetingViewModel
import com.anniyam.meettrackerapp.presentaion.screen.MapPickerScreen
import com.anniyam.meettrackerapp.presentaion.screen.MeetingSchduleScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // Initialize the ViewModel
    val viewModel: MeetingViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "meeting"
    ) {
        // Screen 1: Create or Join Meeting
        composable("meeting") {
            CreateMeetingScreen(
               navController,
                onJoinMeeting = { id, name, mobile ->
                    viewModel.joinMeeting(id, name, mobile) {
                        // After success, navigate to the Map screen
                        navController.navigate("map_screen/$id/$mobile")
                    }
                }
            )
        }
        composable("map_picker_screen"){
            MapPickerScreen(navController)
        }
        composable("create_meeting"){
            MeetingSchduleScreen(navController,
                onMeetingCreated = {
                    meetingId, meetingType, reason, location ->
                    viewModel.createMeeting(meetingId,meetingType,reason,location, onSuccess = {
                        navController.navigate("meeting")
                    })
                })
        }

        // Screen 2: Map Screen
        composable("map_screen/{meetingId}/{mobilenumber}") { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            val mobilenumber = backStackEntry.arguments?.getString("mobilenumber") ?: ""

            MapScreen(navController, meetingId,mobilenumber)
          //  MyMapScreen()
        }
    }
}
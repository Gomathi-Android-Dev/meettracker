package com.anniyam.meettracker.ui.screen

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val db: FirebaseDatabase
) : ViewModel() {

    // Function to Create a New Meeting
    fun createMeeting(meetingId: String, meetingType: String, reason: String, location: String, onSuccess: () -> Unit) {
        val meetingData = mapOf(
            "meetingId" to meetingId,
            "meetingType" to meetingType,
            "reason" to reason,
            "createdAt" to System.currentTimeMillis(),
            "status" to "active"
        )
        val locationData = mapOf(
            "latitude" to location.split(",")[0],
            "longitude" to location.split(",")[1]
        )


        db.reference.child("meetings").child(meetingId)
            .setValue(meetingData)
            .addOnSuccessListener {
                onSuccess()
            }
        db.reference.child("meetings").child(meetingId).child("Location").setValue(locationData)

    }

    // Function to Join an Existing Meeting
    fun joinMeeting(meetingId: String, name: String, mobile: String, onSuccess: () -> Unit) {
        val participantData = mapOf(
            "name" to name,
            "mobile" to mobile,
            "joinedAt" to System.currentTimeMillis()
        )

        // Adds the user to a 'participants' list inside the specific meeting
        db.reference.child("meetings")
            .child(meetingId).child("users").child(mobile)
            .setValue(participantData)
            .addOnSuccessListener {
                onSuccess()
            }
    }
}
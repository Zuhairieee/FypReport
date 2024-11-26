package com.example.shareride2.models


// User data class for Firebase Realtime Database
data class User
    (val username: String, val email: String, val phoneNumber: String)

data class Ride(
    var id: String? = null,
    var goingTo: String = "",
    var leavingFrom: String = "",
    var date: String = "",
    var time: String = "",
    var currentPassengerCount: Int = 0,
    var maxPassengers: Int = 0,
    var userId: String? = null  // New field to store the user ID
)




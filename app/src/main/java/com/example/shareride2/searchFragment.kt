package com.example.shareride2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride2.adapter.RideAdapter
import com.example.shareride2.models.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class searchFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var rideAdapter: RideAdapter
    private lateinit var valueEventListener: ValueEventListener

    private val allRides = mutableListOf<Ride>()
    private val filteredRides = mutableListOf<Ride>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)

        // Setup RecyclerView with initial empty list
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        rideAdapter = RideAdapter(filteredRides, { ride -> confirmRide(ride) }, "Confirm Book")
        recyclerView.adapter = rideAdapter

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("rides")

        // Fetch and observe data from Firebase
        setupFirebaseListener()

        // Add text change listener for search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRides(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    // Setup Firebase listener to fetch rides
    private fun setupFirebaseListener() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return // Exit if fragment is not attached

                allRides.clear()  // Clear previous list of rides

                if (snapshot.exists()) {
                    for (rideSnapshot in snapshot.children) {
                        val ride = rideSnapshot.getValue(Ride::class.java)

                        // Only add rides that are not fully booked
                        if (ride != null && ride.currentPassengerCount < ride.maxPassengers) {
                            allRides.add(ride)  // Add ride only if not fully booked
                        }
                    }

                    filterRides(searchEditText.text.toString())  // Filter rides based on the current search query
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "No rides available.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to load rides: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        database.addValueEventListener(valueEventListener)
    }

    // Filter rides based on search query
    private fun filterRides(query: String) {
        if (!isAdded) return

        filteredRides.clear()

        if (query.isNotEmpty()) {
            filteredRides.addAll(allRides.filter { ride ->
                ride.goingTo.contains(query, ignoreCase = true)
            })
        }

        rideAdapter.notifyDataSetChanged()

        if (filteredRides.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(requireContext(), "No rides found for your search.", Toast.LENGTH_SHORT).show()
        }
    }

    // Confirm ride booking
    private fun confirmRide(ride: Ride) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (ride.currentPassengerCount >= ride.maxPassengers) {
            Toast.makeText(requireContext(), "This ride is full and cannot be booked.", Toast.LENGTH_SHORT).show()
            return
        }

        val bookedRidesDatabase = FirebaseDatabase.getInstance().getReference("bookedRides")
        val newRideRef = bookedRidesDatabase.push()
        ride.id = newRideRef.key
        ride.currentPassengerCount += 1
        ride.userId = userId // Set the userId for the ride booking

        newRideRef.setValue(ride).addOnSuccessListener {
            // Update the ride in the "rides" database to reflect the new passenger count
            database.child(ride.id!!).setValue(ride).addOnSuccessListener {
                // Successfully updated the ride, now remove the old ride from the list
                removeOldRide(ride)
                Toast.makeText(requireContext(), "Ride booked successfully!", Toast.LENGTH_SHORT).show()
                setupFirebaseListener() // Refresh the ride list
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update ride: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to book ride: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Remove the old ride from the list after booking
    private fun removeOldRide(ride: Ride) {
        allRides.removeAll { it.id == ride.id }
        filteredRides.removeAll { it.id == ride.id }
        rideAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(valueEventListener)
    }
}

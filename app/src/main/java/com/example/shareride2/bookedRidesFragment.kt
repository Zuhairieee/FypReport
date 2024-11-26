package com.example.shareride2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride2.adapter.RideAdapter
import com.example.shareride2.models.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class bookedRidesFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var bookedRidesAdapter: RideAdapter
    private lateinit var bookedRidesList: MutableList<Ride>
    private lateinit var recyclerView: RecyclerView

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // The listener for Firebase data changes
    private lateinit var bookedRidesListener: ChildEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booked_rides, container, false)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("bookedRides")
        bookedRidesList = mutableListOf()

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewBookedRides)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookedRidesAdapter = RideAdapter(bookedRidesList, { ride -> cancelRide(ride) }, "Cancel Book")
        recyclerView.adapter = bookedRidesAdapter

        // Load the booked rides for the logged-in user
        loadBookedRides()

        return view
    }

    private fun loadBookedRides() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Listener for Firebase changes
            bookedRidesListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val ride = snapshot.getValue(Ride::class.java)
                    ride?.let {
                        if (it.userId == userId) {
                            bookedRidesList.add(it)
                            bookedRidesAdapter.notifyItemInserted(bookedRidesList.size - 1)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle ride updates (if needed)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val ride = snapshot.getValue(Ride::class.java)
                    ride?.let {
                        bookedRidesList.remove(it)
                        bookedRidesAdapter.notifyDataSetChanged()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle ride move (if needed)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load booked rides: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }

            database.addChildEventListener(bookedRidesListener)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelRide(ride: Ride) {
        ride.id?.let { rideId ->
            database.child(rideId).removeValue().addOnSuccessListener {
                bookedRidesList.remove(ride)
                bookedRidesAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Ride canceled successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to cancel ride: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "Ride ID is null, cannot cancel ride", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the Firebase listener when the view is destroyed
        database.removeEventListener(bookedRidesListener)
    }
}

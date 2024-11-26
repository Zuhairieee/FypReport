package com.example.shareride2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride2.R
import com.example.shareride2.models.Ride

class RideAdapter(
    private val rides: List<Ride>,
    private val onRideAction: (Ride) -> Unit, // Callback for button actions
    private val buttonLabel: String // Label for the button (e.g., "Confirm Book", "Cancel Book")
) : RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        // Inflate item layout for each ride
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_ride_adapter, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rides[position]
        holder.bind(ride, buttonLabel)

        // Set up button click listener to trigger the action callback
        holder.actionButton.setOnClickListener {
            onRideAction(ride)
        }
    }

    override fun getItemCount(): Int = rides.size

    // ViewHolder class for Ride items
    class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val leavingFromTextView: TextView = itemView.findViewById(R.id.leavingFromTextView)
        private val goingToTextView: TextView = itemView.findViewById(R.id.goingToTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val passengersTextView: TextView = itemView.findViewById(R.id.passengersTextView)
        val actionButton: Button = itemView.findViewById(R.id.confirmButton)

        // Bind ride details and button label
        fun bind(ride: Ride, buttonLabel: String) {
            leavingFromTextView.text = ride.leavingFrom ?: "N/A"
            goingToTextView.text = ride.goingTo ?: "N/A"
            dateTextView.text = ride.date ?: "N/A"
            timeTextView.text = ride.time ?: "N/A"
            passengersTextView.text = "Passengers: ${ride.currentPassengerCount ?: 0}"
            actionButton.text = buttonLabel
        }
    }
}

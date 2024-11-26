package com.example.shareride2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.shareride2.models.Ride

class ItemLayout : AppCompatActivity() {

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_ride_adapter, parent, false)
        return RideViewHolder(view)
    }

    inner class RideViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(ride: Ride) {
            itemView.findViewById<TextView>(R.id.leavingFromTextView).text = ride.leavingFrom
            itemView.findViewById<TextView>(R.id.goingToTextView).text = ride.goingTo
            itemView.findViewById<TextView>(R.id.dateTextView).text = ride.date
            itemView.findViewById<TextView>(R.id.passengersTextView).text = ride.currentPassengerCount.toString()  // Corrected here
        }
    }
}


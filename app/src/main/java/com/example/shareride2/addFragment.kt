package com.example.shareride2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.shareride2.models.Ride
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class addFragment : Fragment() {

    private lateinit var database: DatabaseReference

    private lateinit var etLeavingFrom: EditText
    private lateinit var etGoingTo: EditText
    private lateinit var etDate: EditText
    private lateinit var etNoOfPax: EditText
    private lateinit var etTime: EditText
    private lateinit var btnPublish: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        database = FirebaseDatabase.getInstance().getReference("rides")

        etLeavingFrom = view.findViewById(R.id.etLF)
        etGoingTo = view.findViewById(R.id.etGT)
        etDate = view.findViewById(R.id.etDate)
        etNoOfPax = view.findViewById(R.id.etNoOfPax)
        etTime = view.findViewById(R.id.etTime)
        btnPublish = view.findViewById(R.id.buttonSearch)

        etDate.setOnClickListener { showDatePickerDialog() }
        etTime.setOnClickListener { showTimePickerDialog() }
        btnPublish.setOnClickListener { offerRide() }

        return view
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            etDate.setText("$d/${m + 1}/$y")
        }, year, month, day).show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, h, m ->
            etTime.setText(String.format("%02d:%02d", h, m))
        }, hour, minute, true).show()
    }

    private fun offerRide() {
        val leavingFrom = etLeavingFrom.text.toString()
        val goingTo = etGoingTo.text.toString()
        val date = etDate.text.toString()
        val time = etTime.text.toString()
        val maxPassengers = etNoOfPax.text.toString().toIntOrNull()

        if (leavingFrom.isEmpty() || goingTo.isEmpty() || date.isEmpty() || time.isEmpty() || maxPassengers == null) {
            Toast.makeText(context, "Please fill in all fields with valid data", Toast.LENGTH_SHORT).show()
            return
        }

        val rideId = database.push().key ?: return
        val ride = Ride(
            id = rideId,
            leavingFrom = leavingFrom,
            goingTo = goingTo,
            date = date,
            time = time,
            currentPassengerCount = 0,
            maxPassengers = maxPassengers
        )

        database.child(rideId).setValue(ride).addOnSuccessListener {
            Toast.makeText(context, "Ride offered successfully!", Toast.LENGTH_SHORT).show()
            clearFields()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to offer ride. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        etLeavingFrom.text.clear()
        etGoingTo.text.clear()
        etDate.text.clear()
        etNoOfPax.text.clear()
        etTime.text.clear()
    }
}

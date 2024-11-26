package com.example.shareride2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class profileFragment : Fragment() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDateOfBirth: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLogOut: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.editTextTextEmailAddress)
        etAddress = view.findViewById(R.id.editTextText3)
        etPhone = view.findViewById(R.id.editTextPhone)
        etDateOfBirth = view.findViewById(R.id.editTextDate)
        btnSave = view.findViewById(R.id.btnSave)
        btnLogOut = view.findViewById(R.id.btnLogOut)

        // Initialize Firebase Realtime Database and Authentication
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        // Load profile data
        loadProfileData()

        // Date picker for Date of Birth
        etDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        // Save new data
        btnSave.setOnClickListener {
            saveProfileData()
        }

        // Log out when LogOut button is clicked
        btnLogOut.setOnClickListener {
            logOutUser()
        }

        return view
    }

    private fun showDatePickerDialog() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update EditText with selected date
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                etDateOfBirth.setText(formattedDate)
            },
            year, month, day
        )

        // Optional: Set max date to today to prevent future dates
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.child("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // Populate fields with data from Realtime Database
                        etUsername.setText(snapshot.child("name").value.toString())
                        etEmail.setText(snapshot.child("email").value.toString())
                        etAddress.setText(snapshot.child("address").value.toString())
                        etPhone.setText(snapshot.child("phone").value.toString())
                        etDateOfBirth.setText(snapshot.child("date_of_birth").value.toString())
                    } else {
                        Toast.makeText(requireContext(), "No user data found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error loading data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userData = mapOf(
                "name" to etUsername.text.toString().trim(),
                "email" to etEmail.text.toString().trim(),
                "address" to etAddress.text.toString().trim(),
                "phone" to etPhone.text.toString().trim(),
                "date_of_birth" to etDateOfBirth.text.toString().trim()
            )

            db.child("users").child(userId).setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error saving profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logOutUser() {
        auth.signOut()
        val intent = Intent(activity, loginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}

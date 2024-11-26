package com.example.shareride2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class signUpActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnLogin2: Button

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        etName = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignUp = findViewById(R.id.btnSignUp2)
        btnLogin2 = findViewById(R.id.btnLogin2)

        // Sign up button click listener
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                signUpUser(name, email, password)
            }
        }

        // Login button click listener
        btnLogin2.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to sign up the user
    private fun signUpUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignUp", "User created successfully")
                    // Sign-up successful, proceed with Realtime Database
                    saveUserDataToRealtimeDatabase(name, email)
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        Log.e("SignUp", "Sign-up failed: ${exception.message}")
                        Toast.makeText(
                            this,
                            "Sign-up failed: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        exception.printStackTrace()
                    }
                }
            }
    }

    private fun saveUserDataToRealtimeDatabase(name: String, email: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )

        val db = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid
        userId?.let {
            db.child("users").child(it).setValue(user)
                .addOnSuccessListener {
                    Log.d("RealtimeDB", "User data saved successfully")
                    val sharedPreferences =
                        getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putString("username", name)
                        putString("email", email)
                        apply()
                    }
                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()

                    // Start LoginActivity and finish the current SignUpActivity
                    val intent = Intent(this, loginActivity::class.java)
                    startActivity(intent)
                    finish() // Close the SignUpActivity after successful sign-up
                }
                .addOnFailureListener { e ->
                    Log.e("RealtimeDB", "Error saving user data: ${e.message}")
                    Toast.makeText(
                        this,
                        "Error saving user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Log.e("RealtimeDB", "User ID is null. Cannot save data.")
        }
    }
}

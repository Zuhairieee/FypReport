
package com.example.shareride2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class loginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var tvFAQ: TextView
    private lateinit var tvUG: TextView

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvFAQ = findViewById(R.id.tvFAQ)
        tvUG = findViewById(R.id.tvUG)

        // Login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                loginUser(email, password)
            }
        }

        // Sign Up button click listener
        btnSignUp.setOnClickListener {
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }

        // Set the FAQ TextView as clickable
        tvFAQ.setOnClickListener {
            // Navigate to FAQ Activity when clicked
            val intent = Intent(this, FAQActivity::class.java)
            startActivity(intent)

        }
        // Set the FAQ TextView as clickable
        tvUG.setOnClickListener {
            // Navigate to FAQ Activity when clicked
            val intent = Intent(this, UserGuideActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to log in the user
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val db = FirebaseFirestore.getInstance()
                        val userId = it.uid

                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                val userName = document.getString("name")
                                val userEmail = document.getString("email")

                                // Save user data to SharedPreferences
                                val sharedPreferences =
                                    getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                sharedPreferences.edit().apply {
                                    putString("username", userName)
                                    putString("email", userEmail)
                                    apply()
                                }

                                // Navigate to MainActivity after successful login
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Close the LoginActivity
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error retrieving user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

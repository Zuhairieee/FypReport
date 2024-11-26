package com.example.shareride2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class FAQActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq) // Use your actual layout name

        // Initialize the back button
        val backButton: ImageButton = findViewById(R.id.backButton)

        // Set an onClickListener to handle the back button press
        backButton.setOnClickListener {
            // Call onBackPressed() to navigate back to the previous activity
            onBackPressed()
        }
    }
}

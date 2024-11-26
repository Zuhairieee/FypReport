package com.example.shareride2

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class UserGuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_guide) // Use your actual layout name

        // Initialize the back button
        val backButton: ImageButton = findViewById(R.id.backButton2)

        // Set an onClickListener to handle the back button press
        backButton.setOnClickListener {
            // Call onBackPressed() to navigate back to the previous activity
            onBackPressed()
        }
    }
}

package com.example.mymemory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        findViewById<Button>(R.id.btnScores).setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
    }
}



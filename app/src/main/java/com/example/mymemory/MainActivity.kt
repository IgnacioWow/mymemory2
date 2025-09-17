package com.example.mymemory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.activity_main)

        val etName = findViewById<EditText>(R.id.etName)
        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) { Toast.makeText(this,"Ingresa tu nombre",Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            startActivity(Intent(this, PlayerActivity::class.java).putExtra("playerName", name))
        }
        findViewById<Button>(R.id.btnScores).setOnClickListener {
            startActivity(Intent(this, ScoresActivity::class.java))
        }
        }
    }


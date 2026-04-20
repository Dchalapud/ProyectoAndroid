package com.example.actividadandroid.activities.inicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.actividadandroid.R
import com.example.actividadandroid.activities.auth.Login

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btn_comienza).setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

    }
}
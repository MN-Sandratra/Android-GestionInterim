package com.example.gestioninterim

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var toConnect:TextView = findViewById<TextView>(R.id.clickHere)
        toConnect.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, InscriptionActivity::class.java)
            startActivity(intent)
        })
    }
}
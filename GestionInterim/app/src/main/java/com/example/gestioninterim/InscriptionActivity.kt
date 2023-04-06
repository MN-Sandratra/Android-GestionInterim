package com.example.gestioninterim

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class InscriptionActivity : AppCompatActivity() {
    var fragmentManager: FragmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val fragment = FragmentTitleSubTitle()
        fragmentTransaction.add(R.id.title_subtitle_slogan, fragment)
        fragmentTransaction.commit()
    }
}
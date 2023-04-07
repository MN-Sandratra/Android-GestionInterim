package com.example.gestioninterim

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class InscriptionActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val fragment = FragmentChooseInscription()
        fragmentTransaction.add(R.id.containerChooseInscription, fragment)
        fragmentTransaction.commit()

    }
}
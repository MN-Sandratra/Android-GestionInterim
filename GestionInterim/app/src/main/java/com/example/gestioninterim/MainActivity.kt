package com.example.gestioninterim

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.slides.FragmentSlideParent

class MainActivity : AppCompatActivity() {
    var fragmentManager: FragmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val fragment = FragmentSlideParent()
        fragmentTransaction.add(R.id.containerFirstSlides, fragment)
        fragmentTransaction.commit()
    }
}
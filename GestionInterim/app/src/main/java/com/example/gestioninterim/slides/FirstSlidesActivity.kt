package com.example.gestioninterim.slides

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity

class FirstSlidesActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null
    private var pagerAdapter: FragmentStateAdapter? = null
    var round1: ImageView? = null
    var round2: ImageView? = null
    var round3: ImageView? = null
    var skipButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_slides)

        viewPager2 = findViewById(R.id.viewpagerSlides)
        pagerAdapter = ScreenSlidePageAdapter(this)
        viewPager2!!.adapter = pagerAdapter
        round1 = findViewById<View>(R.id.round1) as ImageView
        round2 = findViewById<View>(R.id.round2) as ImageView
        round3 = findViewById<View>(R.id.round3) as ImageView
        skipButton = findViewById<View>(R.id.imageButton) as ImageButton
        val res = resources
        viewPager2!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (viewPager2!!.currentItem) {
                    0 -> {
                        round1!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round_white,
                                        null
                                )
                        )
                        round2!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round,
                                        null
                                )
                        )
                    }
                    1 -> {
                        round1!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round,
                                        null
                                )
                        )
                        round2!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round_white,
                                        null
                                )
                        )
                        round3!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round,
                                        null
                                )
                        )
                    }
                    2 -> {
                        round2!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round,
                                        null
                                )
                        )
                        round3!!.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                        res,
                                    R.drawable.round_white,
                                        null
                                )
                        )
                    }
                }
            }
        })
        skipButton!!.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            }
        }

    private class ScreenSlidePageAdapter(fragmentActivity: FragmentActivity) :
            FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FragmentSlideInterim()
                1 -> FragmentSlideEmployeur()
                2 -> FragmentSlideAgence()
                else -> FragmentSlideInterim()
            }
        }

        override fun getItemCount(): Int {
            return NUM_SLIDES
        }
    }

    companion object {
        private const val NUM_SLIDES = 3
    }

}
package com.example.gestioninterim.utilisateurEmployeur;

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R

class SlidesActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null
    private var pagerAdapter: FragmentStateAdapter? = null
    var round1: ImageView? = null
    var round2: ImageView? = null
    var round3: ImageView? = null
    var skipButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slides_employeur)

        val res = resources
        val intent = intent


        val bienvenueTextView = findViewById<TextView>(R.id.welcomeEmployeur)

        val username = intent.getStringExtra("nom")

        val bienvenueText = res.getString(R.string.welcomeEmployeur, username)
        val formattedText = Html.fromHtml(bienvenueText, Html.FROM_HTML_MODE_LEGACY)
        bienvenueTextView.text = formattedText

        viewPager2 = findViewById(R.id.viewpagerSlides)
        pagerAdapter = ScreenSlidePageAdapter(this, res)
        viewPager2!!.adapter = pagerAdapter
        round1 = findViewById<View>(R.id.round1) as ImageView
        round2 = findViewById<View>(R.id.round2) as ImageView
        round3 = findViewById<View>(R.id.round3) as ImageView
        skipButton = findViewById<View>(R.id.imageButton) as ImageButton

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
            val intent = Intent(this, MainEmployeurActivity::class.java)
            startActivity(intent)
        }
    }

    private class ScreenSlidePageAdapter(fragmentActivity: FragmentActivity, private val res : Resources) : FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val bundle = Bundle()
                    bundle.putString("title", res.getString(R.string.slideTypeAbonnementTitle))
                    bundle.putString("text", res.getString(R.string.slideTypeAbonnementText))
                    val fragment1 = FragmentSlides()
                    fragment1.arguments = bundle
                    fragment1
                }
                1 -> {
                    val bundle = Bundle()
                    bundle.putString("title", res.getString(R.string.slideGestionCandidaturesTitle))
                    bundle.putString("text", res.getString(R.string.slideGestionCandidaturesText))
                    val fragment2 = FragmentSlides()
                    fragment2.arguments = bundle
                    fragment2
                }
                else -> {
                    val bundle = Bundle()
                    bundle.putString("title", res.getString(R.string.slideGestionOffresTitle))
                    bundle.putString("text", res.getString(R.string.slideGestionOffresText))
                    val fragment3 = FragmentSlides()
                    fragment3.arguments = bundle
                    fragment3
                }
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

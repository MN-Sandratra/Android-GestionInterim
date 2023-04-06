package com.example.gestioninterim.slides

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.gestioninterim.FragmentTitleSubTitleSlogan
import com.example.gestioninterim.LoginActivity
import com.example.gestioninterim.R

class FragmentSlideParent : Fragment() {
    private var viewPager2: ViewPager2? = null
    private var pagerAdapter: FragmentStateAdapter? = null
    var round1: ImageView? = null
    var round2: ImageView? = null
    var round3: ImageView? = null
    var skipButton: ImageButton? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_slide_parent, container, false)
        viewPager2 = view.findViewById(R.id.viewpagerSlides)
        pagerAdapter = ScreenSlidePageAdapter(requireActivity())
        viewPager2!!.setAdapter(pagerAdapter)
        round1 = view.findViewById<View>(R.id.round1) as ImageView
        round2 = view.findViewById<View>(R.id.round2) as ImageView
        round3 = view.findViewById<View>(R.id.round3) as ImageView
        skipButton = view.findViewById<View>(R.id.imageButton) as ImageButton
        val res = resources
        viewPager2!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentItem = viewPager2!!.getCurrentItem()
                when (currentItem) {
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
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Ici, nous pouvons ajouter le fragment enfant
        val childFragment = FragmentTitleSubTitleSlogan()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.title_subtitle_slogan, childFragment).commit()
    }

    private inner class ScreenSlidePageAdapter(fragmentActivity: FragmentActivity) :
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
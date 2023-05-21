package com.example.gestioninterim.utilisateurEmployeur

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt

class FragmentAccueilEmployeur : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_employer_accueil_page, container, false)

        viewPager2 = view.findViewById(R.id.viewPager2)
        tabLayout = view.findViewById(R.id.tabLayout)

        val fragments = listOf(
            FragmentAccueilPage1Employeur(),
            FragmentAccueilPage2Employeur()
        )

        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        // Initial tab setup
        TabLayoutMediator(tabLayout, viewPager2) { tab, _ ->
            tab.customView = layoutInflater.inflate(R.layout.custom_tab, null)
        }.attach()

        // Set initial images
        setTabImages(0)

        // Add a page change callback to update the tab images
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setTabImages(position)
            }
        })

        return view
    }

    private fun setTabImages(selectedPosition: Int) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val icon = tab?.customView?.findViewById<ImageView>(R.id.tab_icon)
            if (i == selectedPosition) {
                icon?.setImageResource(R.drawable.round_white)
            } else {
                icon?.setImageResource(R.drawable.round)
            }
        }
    }
}


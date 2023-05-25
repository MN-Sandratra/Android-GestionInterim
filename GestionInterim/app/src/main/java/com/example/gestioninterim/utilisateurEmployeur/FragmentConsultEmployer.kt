package com.example.gestioninterim.utilisateurEmployeur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentConsultEmployer : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_employer_consult, container, false)

        val viewPager2 = view.findViewById<ViewPager2>(R.id.pager)
        val tablayout = view.findViewById<TabLayout>(R.id.tab_layout_employer)
        val pagerAdapter = ScreenSlidePageAdapter(requireActivity())
        viewPager2!!.adapter = pagerAdapter

        TabLayoutMediator(tablayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.top_menu_consult_annonces_deposees)
                1 -> tab.text = resources.getString(R.string.top_menu_consult_candidatures_traiter)
                2 -> tab.text = resources.getString(R.string.top_menu_consult_missions_validées)
            }
        }.attach()


        return view

    }



    private class ScreenSlidePageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FragmentAnnoncesDeposees()
                1 -> FragmentCandidaturesTraiterEmployer()
                2 -> FragmentMissionsValideesEmployer()
                else -> throw IllegalArgumentException("Invalid position: $position")
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
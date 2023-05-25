package com.example.gestioninterim.utilisateurInterimaire

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class FragmentOfferDetails : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_offer_details, container, false)

        val gson = Gson()
        val offerJson = arguments?.getString(ARG_OFFER)
        val offer = gson.fromJson(offerJson, OfferResult::class.java)

        val viewPager2 = view.findViewById<ViewPager2>(R.id.pager)
        val tablayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val pagerAdapter = ScreenSlidePageAdapter(requireActivity(), offerJson)
        viewPager2!!.adapter = pagerAdapter

        TabLayoutMediator(tablayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.offre_details)
                1 -> tab.text = resources.getString(R.string.offre_candidature)
            }
        }.attach()


        return view

    }



    private class ScreenSlidePageAdapter(fragmentActivity: FragmentActivity, private val offerJson: String?) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OffreDetail().apply {
                    arguments = Bundle().apply {
                        putString(ARG_OFFER, offerJson)
                    }
                }
                1 -> FragmentOfferDetailsCandidature().apply {
                    arguments = Bundle().apply {
                        putString(ARG_OFFER, offerJson)
                    }
                }
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }

        override fun getItemCount(): Int {
            return NUM_SLIDES
        }
    }

    companion object {
        private const val NUM_SLIDES = 2
        const val ARG_OFFER = "arg_offer"

        fun newInstance(offer: Offer): FragmentOfferDetails {
            val fragment = FragmentOfferDetails()
            val args = Bundle()
            args.putSerializable(ARG_OFFER, offer)
            fragment.arguments = args
            return fragment
        }
    }


}

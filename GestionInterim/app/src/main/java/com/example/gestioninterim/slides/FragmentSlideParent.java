package com.example.gestioninterim.slides;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.gestioninterim.FragmentTitleSubTitleSlogan;
import com.example.gestioninterim.LoginActivity;
import com.example.gestioninterim.R;


public class FragmentSlideParent extends Fragment {

    private static final int NUM_SLIDES = 3;
    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;

    ImageView round1;
    ImageView round2;
    ImageView round3;

    ImageButton skipButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slide_parent, container, false);


        viewPager2 = view.findViewById(R.id.viewpagerSlides);
        pagerAdapter = new ScreenSlidePageAdapter(getActivity());
        viewPager2.setAdapter(pagerAdapter);

        round1 = (ImageView) view.findViewById(R.id.round1);
        round2 = (ImageView) view.findViewById(R.id.round2);
        round3 = (ImageView) view.findViewById(R.id.round3);
        skipButton = (ImageButton) view.findViewById(R.id.imageButton);

        Resources res = getResources();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int currentItem = viewPager2.getCurrentItem();
                switch(currentItem){
                    case 0:
                        round1.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round_white, null));
                        round2.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round, null));
                        break;
                    case 1:
                        round1.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round, null));
                        round2.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round_white, null));
                        round3.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round, null));
                        break;
                    case 2:
                        round2.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round, null));
                        round3.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.round_white, null));
                        break;
                }
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Ici, nous pouvons ajouter le fragment enfant
        FragmentTitleSubTitleSlogan childFragment = new FragmentTitleSubTitleSlogan();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.title_subtitle_slogan, childFragment).commit();
    }

    private class ScreenSlidePageAdapter extends FragmentStateAdapter{

        public ScreenSlidePageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new FragmentSlideInterim();
                case 1:
                    return new FragmentSlideEmployeur();
                case 2:
                    return new FragmentSlideAgence();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_SLIDES;
        }
    }



}
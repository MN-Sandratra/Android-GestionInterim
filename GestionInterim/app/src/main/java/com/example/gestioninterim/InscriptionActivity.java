package com.example.gestioninterim;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gestioninterim.slides.FragmentSlideParent;

public class InscriptionActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentTitleSubTitle fragment = new FragmentTitleSubTitle();

        fragmentTransaction.add(R.id.title_subtitle_slogan, fragment);

        fragmentTransaction.commit();
    }

}

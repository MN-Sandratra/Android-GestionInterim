package com.example.gestioninterim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.gestioninterim.slides.FragmentSlideParent;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentSlideParent fragment = new FragmentSlideParent();

        fragmentTransaction.add(R.id.containerFirstSlides, fragment);

        fragmentTransaction.commit();

    }
}
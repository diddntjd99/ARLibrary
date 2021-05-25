package com.example.HSB;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.HSB.databinding.ActivityAddreviewBinding;

public class AddReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddreviewBinding binding = ActivityAddreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}

package com.example.HSB;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.HSB.databinding.ActivityReviewBinding;

public class AddReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReviewBinding binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}

package com.example.HSB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.HSB.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public Button button;
    public EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editText = binding.editText;
        button = binding.button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, ListActivity.class);
                it.putExtra("book_name",editText.getText().toString());
                startActivity(it);
            }
        });
    }
}

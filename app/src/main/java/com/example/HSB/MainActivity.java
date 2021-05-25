package com.example.HSB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.HSB.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_button2);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        String name = intent.getStringExtra("name");
        Toast.makeText(this, user_id + name, Toast.LENGTH_SHORT).show();

        binding.find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, ListActivity.class);
                it.putExtra("book_name", binding.editText.getText().toString());
                it.putExtra("user_id", user_id);
                it.putExtra("name", name);
                startActivity(it);
            }
        });

        binding.mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mypageit = new Intent(MainActivity.this, MypageActivity.class);
                mypageit.putExtra("user_id", user_id);
                mypageit.putExtra("name", name);
                startActivity(mypageit);
            }
        });
    }
}

package com.example.HSB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.HSB.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        String name = intent.getStringExtra("name");
        Toast.makeText(this, user_id + name, Toast.LENGTH_SHORT).show();

//        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_button2);
//        getSupportActionBar().setTitle("");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

}

package com.example.HSB;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.HSB.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menubutton_foreground);

        mDrawerLayout = binding.drawerLayout;

        String user_id = StaticData.getStaticDataObject().getUser_id();
        String name = StaticData.getStaticDataObject().getUser_name();
        Toast.makeText(this, user_id + name, Toast.LENGTH_SHORT).show();

        binding.find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, ListActivity.class);
                it.putExtra("book_name", binding.editText.getText().toString());
                startActivity(it);
            }
        });

        /*binding.mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mypageit = new Intent(MainActivity.this, MypageActivity.class);
                mypageit.putExtra("user_id", user_id);
                mypageit.putExtra("name", name);
                startActivity(mypageit);
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }
}

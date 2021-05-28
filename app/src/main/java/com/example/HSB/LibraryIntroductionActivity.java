package com.example.HSB;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.HSB.databinding.ActivityLibraryIntroductionBinding;

public class LibraryIntroductionActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    private String url = "https://hsel.hansung.ac.kr/guide_time.mir";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLibraryIntroductionBinding binding = ActivityLibraryIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        binding.radioGroup.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.open) {
                    url = "https://hsel.hansung.ac.kr/guide_time.mir";
                    binding.webView.loadUrl(url);
                } else if (i == R.id.floor) {
                    url = "https://hsel.hansung.ac.kr/guide_floor.mir";
                    binding.webView.loadUrl(url);
                } else if (i == R.id.location) {
                    url = "https://hsel.hansung.ac.kr/intro_map.mir";
                    binding.webView.loadUrl(url);
                }
            }
        });

        binding.webView.loadUrl(url);
        WebSettings webSettings = binding.webView.getSettings();

        webSettings.setJavaScriptEnabled(true); //자바스크립트 허용
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.mainhome:
                Toast.makeText(this, "homebutton", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}

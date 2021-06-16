package com.example.HSB;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.HSB.databinding.ActivityGroupstudyBinding;
import com.google.android.material.navigation.NavigationView;

public class GroupStudyActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGroupstudyBinding binding = ActivityGroupstudyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        binding.addReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GroupStudyActivity.this);
                LayoutInflater factory = LayoutInflater.from(GroupStudyActivity.this);
                final View view = factory.inflate(R.layout.group_study_alert, null);
                alert.setView(view);
                alert.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                    }
                });
                alert.show();
            }
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                if(id == R.id.mypage){
                    Intent it = new Intent(GroupStudyActivity.this, MypageActivity.class);
                    startActivity(it);
                    finish();
                }
                else if(id == R.id.service){
                    Intent it = new Intent(GroupStudyActivity.this, GroupStudyActivity.class);
                    startActivity(it);
                    finish();
                }
                else if(id == R.id.introduction){
                    Intent it = new Intent(GroupStudyActivity.this, LibraryIntroductionActivity.class);
                    startActivity(it);
                    finish();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.mainhome:
                Intent it = new Intent(this, MainActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(it);
                finish();
                return true;
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

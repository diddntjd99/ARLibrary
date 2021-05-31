package com.example.HSB;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.HSB.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        String user_id = StaticData.getStaticDataObject().getUser_id();
        String name = StaticData.getStaticDataObject().getUser_name();

        TextView userId = (TextView) (binding.navView.getHeaderView(0).findViewById(R.id.user_id));
        TextView userName = (TextView) (binding.navView.getHeaderView(0).findViewById(R.id.user_name));

        userId.setText(user_id);
        userName.setText(name);

        binding.find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(MainActivity.this, ListActivity.class);

                it.putExtra("book_title", binding.editText.getText().toString());
                binding.editText.setText("");
                startActivity(it);
            }
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                binding.editText.setText("");

                if(id == R.id.mypage){
                    Intent it = new Intent(MainActivity.this, MypageActivity.class);
                    startActivity(it);
                }
                else if(id == R.id.service){
                    Intent it = new Intent(MainActivity.this, GroupStudyActivity.class);
                    startActivity(it);
                }
                else if(id == R.id.introduction){
                    Intent it = new Intent(MainActivity.this, LibraryIntroductionActivity.class);
                    startActivity(it);
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.qrmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.qrcode:
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View view = factory.inflate(R.layout.qr_code_alert, null);
                alert.setView(view);
                alert.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                    }
                });
                alert.show();
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

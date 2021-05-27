package com.example.HSB;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.HSB.databinding.ActivityDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menubutton_foreground);

        mDrawerLayout = binding.drawerLayout;

        StaticData sd = StaticData.getStaticDataObject();

        int position = sd.getPosition();
        String user_id = sd.getUser_id();

        JSONObject data = sd.getBook(position);

        try {
            binding.title.setText(data.getString("title"));
            binding.aboutAuthor.setText(data.getString("author"));
            //binding.bookIntroduction.setText(data.getString("introduction"));
            binding.rentalStatus.setText(data.getString("rental"));
            ImageLoadTask task = new ImageLoadTask("http://119.192.49.237/img/" + data.getString("title"), binding.bookImage);
            task.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.addreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DetailActivity.this, ReviewActivity.class);
                startActivity(it);
            }
        });
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
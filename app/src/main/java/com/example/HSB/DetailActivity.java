package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.HSB.databinding.ActivityDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Socket socket;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        String user_id = StaticData.getStaticDataObject().getUser_id();
        JSONObject data = StaticData.getStaticDataObject().getBook();

        try {
            binding.title.setText(data.getString("title"));
            binding.author.setText(data.getString("author"));
            binding.bookIntroduction.setText(data.getString("introduction"));
            binding.publicationYear.setText(data.getString("publication_year"));
            binding.publisher.setText(data.getString("publisher"));
            binding.ISBN.setText(data.getString("ISBN"));
            binding.bookLocation.setText(data.getString("book_location")+"층");
            binding.registrationNumber.setText(data.getString("registration_Number"));
            binding.callNumber.setText(data.getString("call_Number"));
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

        binding.unityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent it = new Intent(MainActivity.this, UnityPlayerActivity.class);
                startActivity(it);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UnityPlayer.UnitySendMessage("Cube", "test", "Test~~~~~");
                    }
                }, 9000);*/
            }
        });

        binding.reservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String getTime = sdf.format(date);

                try {
                    object.put("user_id", user_id);
                    object.put("title", data.getString("title"));
                    object.put("registration_Number", data.getString("registration_Number"));
                    object.put("reservation_date", getTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("reservation_add", object);
            }
        });

        socket.on("reservationSave", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String str = (String) args[0];
                            if (str.equals("Exist")) {
                                Toast.makeText(DetailActivity.this, "이미 예약이 되어있습니다.", Toast.LENGTH_SHORT).show();
                            } else if (str.equals("Save")) {
                                Toast.makeText(DetailActivity.this, "예약되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
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
package com.example.HSB;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.HSB.databinding.ActivityAddreviewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AddReviewActivity extends AppCompatActivity {

    private Socket socket;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddreviewBinding binding = ActivityAddreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String user_id = StaticData.getStaticDataObject().getUser_id();
        JSONObject data = StaticData.getStaticDataObject().getBook();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        try {
            binding.title.setText(data.getString("title"));
            ImageLoadTask task = new ImageLoadTask("http://119.192.49.237/img/" + data.getString("title"), binding.bookImage);
            task.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.addreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("user_id", user_id);
                    object.put("title", data.getString("title"));
                    object.put("review", binding.reviewFieldText.getText());
                    object.put("rating", binding.ratingBar.getRating());
                    socket.emit("book_review_add", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                binding.reviewFieldText.setText("");
                binding.ratingBar.setRating(0);
            }
        });


        socket.on("reviewSave", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String str = (String) args[0];
                            if (str.equals("NoRent")) {
                                Toast.makeText(AddReviewActivity.this, "책을 대여한 기록이 없습니다.", Toast.LENGTH_SHORT).show();
                            } else if (str.equals("Exist")) {
                                Toast.makeText(AddReviewActivity.this, "이미 리뷰를 등록했습니다.", Toast.LENGTH_SHORT).show();
                            } else if (str.equals("Save")) {
                                Toast.makeText(AddReviewActivity.this, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homemenu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }
}
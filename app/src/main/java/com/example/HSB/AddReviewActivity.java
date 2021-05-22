package com.example.HSB;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.HSB.databinding.ActivityAddreviewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

public class AddReviewActivity extends AppCompatActivity {
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddreviewBinding binding = ActivityAddreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent mypageIntent = getIntent();
        String user_id = mypageIntent.getStringExtra("user_id");
        String title = mypageIntent.getStringExtra("title");


        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", user_id);
                    object.put("title", title);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}

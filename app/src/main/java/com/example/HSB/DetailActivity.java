package com.example.HSB;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.HSB.databinding.ActivityDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent detailIntent = getIntent();
        int position = detailIntent.getIntExtra("position", 0);
        String user_id = detailIntent.getStringExtra("user_id");

        BookList bookList = BookList.getBookListObject();
        JSONObject data = bookList.getBook(position);

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
                it.putExtra("position", position);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }
        });
    }
}
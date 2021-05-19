package com.example.HSB;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.HSB.databinding.ActivityDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    ImageLoadTask task;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent detailIntent = getIntent();
        int position = detailIntent.getIntExtra("position", 0);
        BookList bookList = BookList.getBookListObject();
        JSONObject data = bookList.getBook(position);

        try {
            binding.title.setText(data.getString("title"));
            binding.Number.setText(data.getString("book_location"));
            task = new ImageLoadTask("http://119.192.49.237/img/" + data.getString("title"), binding.imageView);
            task.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
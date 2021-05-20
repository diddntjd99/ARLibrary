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
        BookList bookList = BookList.getBookListObject();
        JSONObject data = bookList.getBook(position);

        try {
            binding.title.setText(data.getString("title"));
            binding.Number.setText(data.getString("book_location"));
            ImageLoadTask task = new ImageLoadTask("http://119.192.49.237/img/" + data.getString("title"), binding.imageView);
            task.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DetailActivity.this, ReviewActivity.class);
                it.putExtra("position", position);
                startActivity(it);
            }
        });
    }
}
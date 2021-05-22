package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.HSB.databinding.ActivityReviewBinding;
import com.example.HSB.databinding.ReviewitemBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class RevirewViewHolder extends RecyclerView.ViewHolder {
    ReviewitemBinding itemBinding;

    public RevirewViewHolder(ReviewitemBinding binding) {
        super(binding.getRoot());
        this.itemBinding = binding;
        itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //리사이클러 뷰 클릭 시;

            }
        });
    }
}

class ReviewAdapter extends RecyclerView.Adapter<RevirewViewHolder> {
    private List<JSONObject> reviews;

    ReviewAdapter(List<JSONObject> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public RevirewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ReviewitemBinding itemBinding = ReviewitemBinding.inflate(inflater, parent, false);
        return new RevirewViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RevirewViewHolder holder, int position) {
        JSONObject review = reviews.get(position);
        try {
            holder.itemBinding.id.setText(review.getString("user_id") + " / ");
            holder.itemBinding.review.setText(review.getString("review") + " / ");
            String rating = review.getString("rating");
            holder.itemBinding.ratingBar.setRating(Float.parseFloat(rating));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}

public class ReviewActivity extends AppCompatActivity {
    ArrayList<JSONObject> reviews = new ArrayList<>();
    ReviewAdapter adapter;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReviewBinding binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent reviewIntent = getIntent();
        int position = reviewIntent.getIntExtra("position", 0);
        String user_id = reviewIntent.getStringExtra("userid");

        BookList bookList = BookList.getBookListObject();
        JSONObject data = bookList.getBook(position);

        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new ReviewAdapter(reviews);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        JSONObject object = new JSONObject();
        binding.addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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

        try {
            socket.emit("book_review", data.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.on("return", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray data = (JSONArray) args[0];
                            if(data.length() == 0) {
                                //검색 내용 없음 알림창 생성
                            } else {
                                for (int i = 0; i < data.length(); i++) {
                                    //List에 삽입
                                    reviews.add(data.getJSONObject(i));
                                    adapter.notifyItemInserted(i);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                            if(str.equals("NoRent")) {
                                Toast.makeText(ReviewActivity.this, "책을 대여한 기록이 없습니다.", Toast.LENGTH_SHORT).show();
                            } else if(str.equals("Exist")) {
                                Toast.makeText(ReviewActivity.this, "이미 리뷰를 등록했습니다.", Toast.LENGTH_SHORT).show();
                            } else if(str.equals("Save")) {
                                reviews.add(object);
                                adapter.notifyItemInserted(adapter.getItemCount());
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
}

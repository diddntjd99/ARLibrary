package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
    private String user_id;
    private Socket socket;

    ReviewAdapter(List<JSONObject> reviews, String user_id, Socket socket) {
        this.reviews = reviews;
        this.user_id = user_id;
        this.socket = socket;
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
            holder.itemBinding.id.setText(review.getString("user_id"));
            holder.itemBinding.review.setText(review.getString("review"));
            String rating = review.getString("rating");
            if (user_id.equals(review.getString("user_id"))) {
                holder.itemBinding.delreview.setVisibility(View.VISIBLE);
                holder.itemBinding.delreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            RemoveIndex ri = new RemoveIndex();
                            ri.setIndex(position);

                            JSONObject object = new JSONObject();
                            object.put("user_id", review.getString("user_id"));
                            object.put("title", review.getString("title"));

                            socket.emit("delete_review", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
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
    private ActivityReviewBinding binding;

    private ArrayList<JSONObject> reviews = new ArrayList<>();
    private ReviewAdapter adapter;

    private Socket socket;
    private JSONObject data;
    private String user_id;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent reviewIntent = getIntent();
        int position = reviewIntent.getIntExtra("position", 0);
        user_id = reviewIntent.getStringExtra("user_id");

        BookList bookList = BookList.getBookListObject();
        data = bookList.getBook(position);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menubutton_foreground);

        mDrawerLayout = binding.drawerLayout;


        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.reviewViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ReviewActivity.this, AddReviewActivity.class);
                it.putExtra("position", position);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }
        });

        socket.on("return", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray data = (JSONArray) args[0];
                            if (data.length() == 0) {
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
        socket.on("delete", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reviews.remove(new RemoveIndex().getIndex());
                        adapter.notifyItemRemoved(new RemoveIndex().getIndex());
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        reviews = new ArrayList<>();
        adapter = new ReviewAdapter(reviews, user_id, socket);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        try {
            socket.emit("book_review", data.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
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

class RemoveIndex{
    private static int index;
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
}
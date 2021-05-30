package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
                            RemoveReviewIndex ri = new RemoveReviewIndex();
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

        user_id = StaticData.getStaticDataObject().getUser_id();
        data = StaticData.getStaticDataObject().getBook();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

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
                startActivity(it);
            }
        });

        try {
            binding.title.setText(data.getString("title"));
            ImageLoadTask task = new ImageLoadTask("http://119.192.49.237/img/" + data.getString("title"), binding.bookImage);
            task.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.on("book_review_return", new Emitter.Listener() {
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
        socket.on("delete_review_return", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reviews.remove(new RemoveReviewIndex().getIndex());
                        adapter.notifyItemRemoved(new RemoveReviewIndex().getIndex());
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

class RemoveReviewIndex{
    private static int index;
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
}
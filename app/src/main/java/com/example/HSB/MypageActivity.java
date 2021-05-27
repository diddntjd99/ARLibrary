package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.HSB.databinding.ActivityMypageBinding;
import com.example.HSB.databinding.RentalbooklistitemBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class RentalBookViewHolder extends RecyclerView.ViewHolder {
   RentalbooklistitemBinding itemBinding;

    public RentalBookViewHolder(RentalbooklistitemBinding binding) {
        super(binding.getRoot());
        this.itemBinding = binding;
        itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //리사이클러 뷰 클릭 시
                int i = getAdapterPosition();
                if (i != RecyclerView.NO_POSITION) {
                }
            }
        });
    }
}

class RentalBookAdapter extends RecyclerView.Adapter<RentalBookViewHolder> {
    private List<JSONObject> rentalbooks;


    RentalBookAdapter(List<JSONObject> rentalbooks) {
        this.rentalbooks = rentalbooks;
    }

    @NonNull
    @Override
    public RentalBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RentalbooklistitemBinding itemBinding = RentalbooklistitemBinding.inflate(inflater, parent, false);
        return new RentalBookViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalBookViewHolder holder, int position) {
        JSONObject book = rentalbooks.get(position);
        try {
            holder.itemBinding.bookTitle.setText(book.getString("title"));
            holder.itemBinding.rentalDate.setText(book.getString("rental_date"));
            holder.itemBinding.returnDate.setText(book.getString("return_date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return rentalbooks.size();
    }
}

public class MypageActivity extends AppCompatActivity {
    ArrayList<JSONObject> rentalbooks = new ArrayList<>();
    RentalBookAdapter adapter;

    private DrawerLayout mDrawerLayout;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMypageBinding binding = ActivityMypageBinding.inflate(getLayoutInflater());
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menubutton_foreground);

        mDrawerLayout = binding.drawerLayout;

        String user_id = StaticData.getStaticDataObject().getUser_id();
        String name = StaticData.getStaticDataObject().getUser_name();


        adapter = new RentalBookAdapter(rentalbooks);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        socket.emit("user_rental_history", user_id);
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
                                    rentalbooks.add(data.getJSONObject(i));
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

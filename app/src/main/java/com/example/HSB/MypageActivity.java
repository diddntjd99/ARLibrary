package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private String user_id;

    RentalBookAdapter(List<JSONObject> rentalbooks, String user_id) {
        this.rentalbooks = rentalbooks;
        this.user_id = user_id;
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
            holder.itemBinding.bookTitle.setText(book.getString("title") + " / ");
            holder.itemBinding.rentalDate.setText(book.getString("rental_date") + " / ");
            holder.itemBinding.returnDate.setText(book.getString("return_date"));

            holder.itemBinding.addreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(holder.itemBinding.getRoot().getContext(), AddReviewActivity.class);
                    try {
                        it.putExtra("user_id", user_id);
                        it.putExtra("title", book.getString("title"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    holder.itemBinding.getRoot().getContext().startActivity(it);
                }
            });
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

        Intent mypageIntent = getIntent();
        String user_id = mypageIntent.getStringExtra("userid");
        String name = mypageIntent.getStringExtra("name");

        binding.name.setText(name+"님의 대출 도서 목록");

        adapter = new RentalBookAdapter(rentalbooks, user_id);
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
}

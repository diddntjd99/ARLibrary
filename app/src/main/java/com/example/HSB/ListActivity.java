package com.example.HSB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.HSB.databinding.ActivityRecyclerviewBinding;
import com.example.HSB.databinding.ItemBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class MyViewHolder extends RecyclerView.ViewHolder {
    ItemBinding itemBinding;

    public MyViewHolder(ItemBinding binding) {
        super(binding.getRoot());
        this.itemBinding = binding;
        itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //리사이클러 뷰 클릭 시
                int i = getAdapterPosition();
                if (i != RecyclerView.NO_POSITION) {
                    Toast.makeText(itemBinding.getRoot().getContext()
                            , itemBinding.textView.getText().toString()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<JSONObject> books;

    MyAdapter(List<JSONObject> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemBinding itemBinding = ItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        JSONObject book = books.get(position);
        try {
            holder.itemBinding.textView.setText(book.getString("title"));

            Log.i("ttt", book.getString("encodeBook"));

            //책 이미지 - 인코딩된 문자열을 디코딩하여 다시 bitmap 형식으로 변환
            byte[] imgByte = Base64.decode(book.getString("encodeBook"), Base64.DEFAULT);
            //Bitmap img = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(imgByte);
            Bitmap img = BitmapFactory.decodeStream(byteStream) ;

            holder.itemBinding.image.setImageBitmap(img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}

public class ListActivity extends AppCompatActivity {
    ArrayList<JSONObject> books = new ArrayList<>();
    MyAdapter adapter;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRecyclerviewBinding binding = ActivityRecyclerviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new MyAdapter(books);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        Intent secondIntent = getIntent();
        String book_name = secondIntent.getStringExtra("book_name");

        socket.emit("book_name", book_name);
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
                                    books.add(data.getJSONObject(i));
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
}

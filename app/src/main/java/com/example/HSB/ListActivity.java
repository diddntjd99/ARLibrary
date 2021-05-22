package com.example.HSB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.HSB.databinding.ActivityBooklistBinding;
import com.example.HSB.databinding.BooklistitemBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class BookViewHolder extends RecyclerView.ViewHolder {
    BooklistitemBinding itemBinding;

    public BookViewHolder(BooklistitemBinding binding) {
        super(binding.getRoot());
        this.itemBinding = binding;
        itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //리사이클러 뷰 클릭 시
                int i = getAdapterPosition();
                if (i != RecyclerView.NO_POSITION) {
                    Intent it = new Intent(itemBinding.getRoot().getContext(), DetailActivity.class);
                    it.putExtra("position", i);
                    it.putExtra("user_id", new GetUserId().getUserId());
                    itemBinding.getRoot().getContext().startActivity(it);
                }
            }
        });
    }
}

class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<JSONObject> books;

    BookAdapter(List<JSONObject> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        BooklistitemBinding itemBinding = BooklistitemBinding.inflate(inflater, parent, false);
        return new BookViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        JSONObject book = books.get(position);
        try {
            holder.itemBinding.textView.setText(book.getString("title"));

            ImageLoadTask task = new ImageLoadTask("http://119.192.49.237/img/" + book.getString("title"), holder.itemBinding.image);
            task.execute();
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
    BookAdapter adapter;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBooklistBinding binding = ActivityBooklistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            socket = IO.socket("http://119.192.49.237/");
            //http://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent secondIntent = getIntent();
        String book_name = secondIntent.getStringExtra("book_name");
        String user_id = secondIntent.getStringExtra("user_id");
        GetUserId gui = new GetUserId();
        gui.setUserId(user_id);

        adapter = new BookAdapter(books);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

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

                                //books 라는 JSON List 싱글턴 클래스에 넘겨주기
                                BookList bl = BookList.getBookListObject();
                                bl.setBooks(books);
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

class GetUserId {
    private static String userId;
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId(){
        return userId;
    }
}
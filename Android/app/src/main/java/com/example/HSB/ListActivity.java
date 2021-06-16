package com.example.HSB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.HSB.databinding.ActivityBooklistBinding;
import com.example.HSB.databinding.BooklistitemBinding;
import com.google.android.material.navigation.NavigationView;

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
                    StaticData.getStaticDataObject().setBook(StaticData.getStaticDataObject().getBooks().get(i));
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
            holder.itemBinding.author.setText(book.getString("author"));
            holder.itemBinding.publisher.setText(book.getString("publisher"));
            holder.itemBinding.publicationYear.setText(book.getString("publication_year"));
            holder.itemBinding.callNumber.setText(book.getString("call_Number"));
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

    private DrawerLayout mDrawerLayout;

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

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 title 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        Intent secondIntent = getIntent();
        String book_name = secondIntent.getStringExtra("book_title");

        adapter = new BookAdapter(books);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        binding.find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                books = new ArrayList<>();
                adapter = new BookAdapter(books);
                binding.recyclerview.setAdapter(adapter);
                binding.recyclerview.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                binding.recyclerview.setHasFixedSize(true);

                socket.emit("book_find", binding.editText.getText());
            }
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                if(id == R.id.mypage){
                    Intent it = new Intent(ListActivity.this, MypageActivity.class);
                    startActivity(it);
                }
                else if(id == R.id.service){
                    Intent it = new Intent(ListActivity.this, GroupStudyActivity.class);
                    startActivity(it);
                }
                else if(id == R.id.introduction){
                    Intent it = new Intent(ListActivity.this, LibraryIntroductionActivity.class);
                    startActivity(it);
                }
                return true;
            }
        });

        socket.emit("book_find", book_name);
        socket.on("book_find_return", new Emitter.Listener() {
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
                                binding.bookCount.setText("총 "+ adapter.getItemCount() +"건이 검색되었습니다.");
                                //books 라는 JSON List 싱글턴 클래스에 넘겨주기
                                StaticData.getStaticDataObject().setBooks(books);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homemenu, menu);
        return true;
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
package com.example.HSB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.HSB.databinding.ActivityMypageBinding;
import com.example.HSB.databinding.RentalbooklistitemBinding;
import com.example.HSB.databinding.ReservationbooklistitemBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class RentalHistoryViewHolder extends RecyclerView.ViewHolder {
    RentalbooklistitemBinding itemBinding;

    public RentalHistoryViewHolder(RentalbooklistitemBinding binding) {
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

class RentalHistoryAdapter extends RecyclerView.Adapter<RentalHistoryViewHolder> {
    private List<JSONObject> rentalbooks;
    private Socket socket;

    RentalHistoryAdapter(List<JSONObject> rentalbooks, Socket socket) {
        this.rentalbooks = rentalbooks;
        this.socket = socket;
    }

    @NonNull
    @Override
    public RentalHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RentalbooklistitemBinding itemBinding = RentalbooklistitemBinding.inflate(inflater, parent, false);

        return new RentalHistoryViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalHistoryViewHolder holder, int position) {
        JSONObject book = rentalbooks.get(position);
        try {
            holder.itemBinding.bookTitle.setText(book.getString("title"));
            holder.itemBinding.rentalDate.setText(book.getString("rental_date"));
            holder.itemBinding.returnDate.setText(book.getString("return_date"));
            holder.itemBinding.booknum.setText(book.getString("registration_Number"));

            String str = book.getString("return_date");

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String getTime = sdf.format(date);

            if(str.compareTo(getTime) >= 0)  //반납일이 현재를 지나지 않았을 때
            {
                holder.itemBinding.button.setVisibility(View.VISIBLE);
            }else{
                holder.itemBinding.button.setVisibility(View.INVISIBLE);
            }

            holder.itemBinding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date to =transFormat.parse(str);   //날짜 문자열 date형으로
                        Calendar cal = Calendar.getInstance();
                        //반납날짜에서 2주 지나게 계산
                        cal.setTime(to);
                        cal.add(Calendar.DAY_OF_WEEK, 14);
                        String return_date = transFormat.format(cal.getTime());

                        JSONObject object = new JSONObject();  //json객체 만들어서 바뀐 반납날짜 저장하고 서버로 보내기
                        object.put("user_id", book.getString("user_id"));
                        object.put("title", book.getString("title"));
                        object.put("return_date", return_date);
                        Log.i("TAG", return_date);
                        holder.itemBinding.returnDate.setText(return_date); //바뀐날짜 업데이트트
                        socket.emit("update_return_date", object);
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
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

class ReservationViewHolder extends RecyclerView.ViewHolder {
    ReservationbooklistitemBinding itemBinding;

    public ReservationViewHolder(ReservationbooklistitemBinding binding) {
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

class ReservationAdapter extends RecyclerView.Adapter<ReservationViewHolder> {
    private List<JSONObject> reservations;
    private Socket socket;

    ReservationAdapter(List<JSONObject> rentalbooks, Socket socket) {
        this.reservations = rentalbooks;
        this.socket = socket;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ReservationbooklistitemBinding itemBinding = ReservationbooklistitemBinding.inflate(inflater, parent, false);
        return new ReservationViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        JSONObject book = reservations.get(position);
        try {
            holder.itemBinding.bookTitle.setText(book.getString("title"));
            holder.itemBinding.reservationDate.setText(book.getString("reservation_date"));
            holder.itemBinding.booknum.setText(book.getString("registration_Number"));
            holder.itemBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RemoveReservationIndex ri = new RemoveReservationIndex();
                    ri.setIndex(position);

                    JSONObject object = new JSONObject();
                    try {
                        object.put("user_id", book.getString("user_id"));
                        object.put("title", book.getString("title"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("delete_reservation", object);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
}

public class MypageActivity extends AppCompatActivity {
    ArrayList<JSONObject> rentalbooks = new ArrayList<>();
    ArrayList<JSONObject> reservations = new ArrayList<>();

    RentalHistoryAdapter rental_adapter;
    ReservationAdapter reservation_adapter;

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        mDrawerLayout = binding.drawerLayout;

        String user_id = StaticData.getStaticDataObject().getUser_id();
        String name = StaticData.getStaticDataObject().getUser_name();

        rental_adapter = new RentalHistoryAdapter(rentalbooks, socket);
        binding.recyclerviewRentalHistory.setAdapter(rental_adapter);
        binding.recyclerviewRentalHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewRentalHistory.setHasFixedSize(true);

        reservation_adapter = new ReservationAdapter(reservations, socket);
        binding.recyclerviewReservation.setAdapter(reservation_adapter);
        binding.recyclerviewReservation.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewReservation.setHasFixedSize(true);

        binding.radioGroup.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rental_list) {
                    binding.linear1.setVisibility(View.VISIBLE);
                    binding.recyclerviewRentalHistory.setVisibility(View.VISIBLE);
                    binding.linear2.setVisibility(View.GONE);
                    binding.recyclerviewReservation.setVisibility(View.GONE);
                } else if (i == R.id.reservation_list) {
                    binding.linear1.setVisibility(View.GONE);
                    binding.recyclerviewRentalHistory.setVisibility(View.GONE);
                    binding.linear2.setVisibility(View.VISIBLE);
                    binding.recyclerviewReservation.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                if(id == R.id.mypage){
                    Intent it = new Intent(MypageActivity.this, MypageActivity.class);
                    startActivity(it);
                    finish();
                }
                else if(id == R.id.service){
                    Intent it = new Intent(MypageActivity.this, GroupStudyActivity.class);
                    startActivity(it);
                    finish();
                }
                else if(id == R.id.introduction){
                    Intent it = new Intent(MypageActivity.this, LibraryIntroductionActivity.class);
                    startActivity(it);
                    finish();
                }
                return true;
            }
        });

        socket.emit("user_rental_history", user_id);
        socket.on("user_rental_history_return", new Emitter.Listener() {
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
                                    rental_adapter.notifyItemInserted(i);
                                }

                                binding.rentalList.setChecked(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.emit("reservation", user_id);
        socket.on("reservation_return", new Emitter.Listener() {
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
                                    reservations.add(data.getJSONObject(i));
                                    reservation_adapter.notifyItemInserted(i);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        socket.on("delete_reservation_return", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reservations.remove(new RemoveReservationIndex().getIndex());
                        reservation_adapter.notifyItemRemoved(new RemoveReservationIndex().getIndex());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homemenu, menu);
        return true;
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

class RemoveReservationIndex{
    private static int index;
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
}
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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


    RentalHistoryAdapter(List<JSONObject> rentalbooks) {
        this.rentalbooks = rentalbooks;
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

        rental_adapter = new RentalHistoryAdapter(rentalbooks);
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
                            binding.rentalList.setChecked(true);
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
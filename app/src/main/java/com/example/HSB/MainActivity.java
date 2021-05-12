package com.example.HSB;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    public Button button;
    public TextView textView;
    private Socket socket;
    public EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            socket = IO.socket("http://119.192.49.237/");
            //http:://14.53.49.163
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("book_name", editText.getText().toString());

                socket.on("return", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject data = (JSONObject) args[0];
                                    textView.setText(data.getString("title"));

                                    //Toast.makeText(MainActivity.this, "학번을 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}

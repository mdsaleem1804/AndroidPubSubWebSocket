package com.amazonaws.demo.androidpubsubwebsocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class RoomList extends Activity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_list);
        final Button buttonRoom1 = findViewById(R.id.room1);
        final Button buttonRoom2 = findViewById(R.id.room2);
        buttonRoom1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SingleRoom.class);
                startActivity(intent);
            }
        });
        buttonRoom2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PubSubActivity.class);
                startActivity(intent);
            }
        });
    }
}
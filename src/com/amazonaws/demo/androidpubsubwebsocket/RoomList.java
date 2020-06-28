package com.amazonaws.demo.androidpubsubwebsocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomList extends AppCompatActivity {
    GridView simpleGrid;
    String rooms[] = {"ROOM-1","ROOM-2","ROOM-3","ROOM-4","ROOM-5","ROOM-6","ROOM-7","ROOM-8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.room_list);
            simpleGrid = (GridView) findViewById(R.id.simpleGridView); // init GridView
            // Create an object of CustomAdapter and set Adapter to GirdView
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), rooms);
            simpleGrid.setAdapter(customAdapter);

            simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // set an Intent to Another Activity
                    Intent intent = new Intent(RoomList.this, SingleRoom.class);
                    intent.putExtra("room_name", rooms[position]); // put image data in Intent
                    startActivity(intent); // start Intent
                }
            });
        } catch (Exception ex) {
String xError =ex.toString();
        }
    }
}
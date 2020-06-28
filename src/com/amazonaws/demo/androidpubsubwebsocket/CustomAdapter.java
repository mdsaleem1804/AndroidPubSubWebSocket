package com.amazonaws.demo.androidpubsubwebsocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String logos[];
    LayoutInflater inflter;
    public CustomAdapter(Context applicationContext, String[] logos) {
        this.context = applicationContext;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return logos.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try{

        view = inflter.inflate(R.layout.grid_view_items, null); // inflate the layout
          TextView icon = (TextView) view.findViewById(R.id.icon); // get the reference of ImageView
        icon.setText(logos[i]); // set logo images
        } catch (Exception ex) {
            String xError =ex.toString();

        }
        return view;

    }
}
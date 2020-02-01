package com.shawn.researjour;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

    Context context;
    int[]category_items;
    String [] getCategory_items;
    private LayoutInflater inflater;

    public CustomAdapter(Context context,int[] category_items, String [] getCategory_items) {
        this.context=context;
        this.category_items=category_items;
        this.getCategory_items=getCategory_items;
    }

    @Override
    public int getCount() {
        return getCategory_items.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.grid_itemsview,parent,false);
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.catImage_id);
        TextView textView=(TextView) convertView.findViewById(R.id.catText_id);

        //setting the resources for Image view and Text View
        imageView.setImageResource(category_items[position]);
        textView.setText(getCategory_items[position]);

        return convertView;
    }
}

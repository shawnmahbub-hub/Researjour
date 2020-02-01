package com.shawn.researjour;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CategorySelection extends AppCompatActivity {

    //declare variables for the components in the category selection activity
    GridView gridView_Category;
    Button nextButton;

    //load the image from the drawable for the grid items
    int []category_items={R.drawable.cat_1,R.drawable.cat_2,
                            R.drawable.cat_3,R.drawable.cat_4,R.drawable.cat_5,R.drawable.cat_6,
                            R.drawable.cat_7,R.drawable.cat_8};
    //declare an array for getting the category name
    String[] getCategory_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);
        getCategory_items=getResources().getStringArray(R.array.category_items);

        //finding the id of the grid view
        gridView_Category=(GridView)findViewById(R.id.categoryItem_id);
        nextButton=(Button)findViewById(R.id.next_btn_id);

        //custom adapter for parsing the grid item view
        CustomAdapter customAdaper=new CustomAdapter(this,category_items,getCategory_items);
        gridView_Category.setAdapter(customAdaper);

        gridView_Category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value=getCategory_items[position];
                Toast.makeText(CategorySelection.this, value, Toast.LENGTH_SHORT).show();
            }
        });


    }
}

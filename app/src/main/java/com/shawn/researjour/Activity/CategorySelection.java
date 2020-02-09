package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.shawn.researjour.CustomAdapter;
import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class CategorySelection extends AppCompatActivity {

    //declare variables for the components in the category selection activity
    GridView gridView_Category;
    Button cat_nextButton;

    //load the image from the drawable for the grid items
    int []category_items={R.drawable.cat_1,R.drawable.cat_2,
                            R.drawable.cat_3,R.drawable.cat_4,R.drawable.cat_5,R.drawable.cat_6,
                            R.drawable.cat_7,R.drawable.cat_8,R.drawable.cat_1,R.drawable.cat_6};
    //declare an array for getting the category name
    String[] getCategory_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);
        getCategory_items=getResources().getStringArray(R.array.category_items);

        //finding the id of the grid view
        gridView_Category=(GridView)findViewById(R.id.categoryItem_id);
        cat_nextButton=(Button)findViewById(R.id.cat_next_button_id);

        //custom adapter for parsing the grid item view
        CustomAdapter customAdapter=new CustomAdapter(this,category_items,getCategory_items);
        gridView_Category.setAdapter(customAdapter);

        gridView_Category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value=getCategory_items[position];
                Toast.makeText(CategorySelection.this, value, Toast.LENGTH_SHORT).show();

            }
        });

        cat_nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUsertoSubjectChoiceActivity();
            }
        });


    }

    private void sendUsertoSubjectChoiceActivity() {
        Intent intent=new Intent(this, SubjectChoice.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

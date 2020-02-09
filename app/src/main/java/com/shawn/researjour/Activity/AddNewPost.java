package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class AddNewPost extends AppCompatActivity {

    Toolbar newPostToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        newPostToolbar = findViewById(R.id.addNewPostPageToolbar);
        setSupportActionBar(newPostToolbar);

        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add New Research");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home){
            sendUserToHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToHomeActivity() {

        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}

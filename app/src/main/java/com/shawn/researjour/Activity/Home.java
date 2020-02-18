package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private ImageButton addNewPostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addNewPostBtn=(ImageButton)findViewById(R.id.addPost_button_id);

        addNewPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToAddNewPostActivity();
            }
        });
    }

    private void sendUserToAddNewPostActivity() {
        Intent intent=new Intent(this,AddNewPost.class);
        startActivity(intent);
    }
}

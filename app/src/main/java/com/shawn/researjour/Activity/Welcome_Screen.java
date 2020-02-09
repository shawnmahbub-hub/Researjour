package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome_Screen extends AppCompatActivity {

    Button login,registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome__screen);

        //find the id's of the button inside the welcome screen
        login=findViewById(R.id.login_button_id);
        registration=findViewById(R.id.reg_button_id);

        //start intent activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Welcome_Screen.this, Login.class);
                startActivity(intent);
                Toast.makeText(Welcome_Screen.this, "Login Activity", Toast.LENGTH_SHORT).show();
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Welcome_Screen.this, Registration.class);
                startActivity(intent);
                Toast.makeText(Welcome_Screen.this, "Registration Activity", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.shawn.researjour.R;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome_Screen extends AppCompatActivity {

    Button loginBtn,registrationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome__screen);

        //find the buttons id
        loginBtn=findViewById(R.id.login_button_id);
        registrationBtn=findViewById(R.id.reg_button_id);

        //start intent activity
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegistrationActivity();
            }
        });
    }

    //method for sending the user to Login activity
    private void sendUserToLoginActivity() {
        Intent intent=new Intent(Welcome_Screen.this, Login.class);
        startActivity(intent);
        Toast.makeText(Welcome_Screen.this, "Login Activity", Toast.LENGTH_SHORT).show();
    }

    //method for sending the user to Registration activity
    private void sendUserToRegistrationActivity() {
        Intent intent=new Intent(Welcome_Screen.this, Registration.class);
        startActivity(intent);
        Toast.makeText(Welcome_Screen.this, "Registration Activity", Toast.LENGTH_SHORT).show();
    }
}
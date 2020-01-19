package com.shawn.researjour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    TextView forgot_pass, sign_up;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*finding the id of the declared variables */
        forgot_pass=findViewById(R.id.forgot_pass_id);
        sign_up=findViewById(R.id.sign_up_text_id);
        login=findViewById(R.id.login_button_id);

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this, Forgot_pass_main.class);
                startActivity(intent);
                Toast.makeText(Login.this, "Forgot_pass_main Activity", Toast.LENGTH_SHORT).show();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this, Registration.class);
                startActivity(intent);
                Toast.makeText(Login.this, "Registration Activity", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, Feed.class);
                startActivity(intent);
                Toast.makeText(Login.this, "Feed Activity", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

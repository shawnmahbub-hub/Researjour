package com.shawn.researjour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    EditText email, password;
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
        email=findViewById(R.id.emai_editText_id);
        password=findViewById(R.id.password_editText_id);

        /*login text watcher for empty edit text field*/
        email.addTextChangedListener(loginTextWatcher);
        password.addTextChangedListener(loginTextWatcher);

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

    /*method for login text watcher*/
    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userNameInput=email.getText().toString().trim();
            String passwordInput=password.getText().toString().trim();

            //setting the button enabled if the edit text field is not empty
            login.setEnabled(!userNameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}

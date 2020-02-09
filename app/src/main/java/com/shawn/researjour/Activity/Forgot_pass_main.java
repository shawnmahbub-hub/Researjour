package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class Forgot_pass_main extends AppCompatActivity {

    Button next;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_main);

        next=findViewById(R.id.next_btn_id);
        email=findViewById(R.id.emai_editText_id);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Forgot_pass_main.this, VerifyCode.class);
                startActivity(intent);
            }
        });

        /*login text watcher for empty edit text field*/
        email.addTextChangedListener(loginTextWatcher);


    }

    /*method for login text watcher*/
    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userNameInput=email.getText().toString().trim();

            //setting the button enabled if the edit text field is not empty
            next.setEnabled(!userNameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}

package com.shawn.researjour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Retype_new_password extends AppCompatActivity {

    Button finish;
    EditText newPass, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retype_new_password);

        finish=findViewById(R.id.finish_btn_id);
        newPass=findViewById(R.id.new_pass_id);
        confirmPass=findViewById(R.id.confirmPassword_editText_id);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Retype_new_password.this, Login.class);
                startActivity(intent);
            }
        });

        /*login text watcher for empty edit text field*/
        newPass.addTextChangedListener(loginTextWatcher);
        confirmPass.addTextChangedListener(loginTextWatcher);
    }

    /*method for login text watcher*/
    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userNameInput=newPass.getText().toString().trim();
            String passwordInput=confirmPass.getText().toString().trim();

            //setting the button enabled if the edit text field is not empty
            finish.setEnabled(!userNameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}

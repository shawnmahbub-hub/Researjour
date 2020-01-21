package com.shawn.researjour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class VerifyCode extends AppCompatActivity {

    Button next;
    EditText verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        next=findViewById(R.id.next_btn_id);
        verify=findViewById(R.id.verify_editText_id);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(VerifyCode.this, Retype_new_password.class);
                startActivity(intent);
            }
        });

        /*login text watcher for empty edit text field*/
        verify.addTextChangedListener(loginTextWatcher);

    }

    /*method for login text watcher*/
    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userNameInput=verify.getText().toString().trim();

            //setting the button enabled if the edit text field is not empty
            next.setEnabled(!userNameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}

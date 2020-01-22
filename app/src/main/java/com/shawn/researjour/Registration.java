package com.shawn.researjour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Registration extends AppCompatActivity {

    /*declaring variables for the components
    inside the registration activity
     */
    ImageButton back_button,google,fb,phone;
    EditText firstName, lastName, email, password, confirmPassword;
    Button registration;
    TextView login,terms;
    CheckBox checkBox;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //finding the id's of the components
        back_button=findViewById(R.id.back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registration.this, Welcome_Screen.class);
                startActivity(intent);
            }
        });

        login=findViewById(R.id.logintext_id);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

        firstName=findViewById(R.id.firstName_editText_id);
        lastName=findViewById(R.id.lastName_editText_id);
        email=findViewById(R.id.emai_editText_id);
        terms=findViewById(R.id.Terms_id);
        password=findViewById(R.id.password_editText_id);
        confirmPassword=findViewById(R.id.confirmPassword_editText_id);
        registration=findViewById(R.id.reg_button_id);
        checkBox=findViewById(R.id.checkbox_id);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registration.this, IntroActivity.class);
                startActivity(intent);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registration.this, Terms.class);
                startActivity(intent);
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String emailInput = email.getText().toString().trim();
                String passwordInput=password.getText().toString().trim();
                String firstNameInput=firstName.getText().toString().trim();
                String lastNameInput=lastName.getText().toString().trim();
                String confirmPassInput=confirmPassword.getText().toString().trim();

                if (b){
                    flag = true;
                    registration.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty() && !firstNameInput.isEmpty()
                            && !lastNameInput.isEmpty() && !confirmPassInput.isEmpty() && flag);
                }else {
                    registration.setEnabled(false);
                    flag = false;
                }
                flag = false;
            }
        });

    }




}

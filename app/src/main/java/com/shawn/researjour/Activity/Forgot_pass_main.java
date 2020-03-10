package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.shawn.researjour.R;

import androidx.annotation.NonNull;
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
                validateInputs();
            }
        });

        /*login text watcher for empty edit text field*/
        email.addTextChangedListener(loginTextWatcher);

    }

    private void validateInputs() {
        String emailInput = email.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            //shake animation
            Animation animShake = AnimationUtils.loadAnimation(Forgot_pass_main.this, R.anim.shake);
            email.startAnimation(animShake);

            email.setError("Invalid Email");
            email.requestFocus();
            return;
        }else {
            beginRecovery(emailInput);
        }
    }

    private void beginRecovery(String recoveryEmail) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(recoveryEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Forgot_pass_main.this, "Email Sent..", Toast.LENGTH_SHORT).show();
                            sendUserToLoginActivity();
                        }else {
                            Toast.makeText(Forgot_pass_main.this, "Email send failed..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Forgot_pass_main.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendUserToLoginActivity() {
        //method for sending the user to the login activity
            Intent intent=new Intent(Forgot_pass_main.this, Login.class);
            startActivity(intent);
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

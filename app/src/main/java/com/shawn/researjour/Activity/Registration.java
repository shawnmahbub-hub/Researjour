package com.shawn.researjour.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shawn.researjour.R;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    /*declaring variables for the components inside the registration activity*/
    ImageView passMatched,passNotMatched;
    ImageButton google,fb,passVisibility;
    EditText reg_email, password, confirmPassword;
    Button registration;
    TextView login,terms;
    CheckBox checkBox;
    boolean flag;
    ProgressDialog loadingBar;
    private boolean isShowPassword;

    //firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //init fireBase
        mAuth=FirebaseAuth.getInstance();
        //loading bar
        loadingBar=new ProgressDialog(this);

        reg_email=findViewById(R.id.reg_email_editText_id);
        terms=findViewById(R.id.Terms_id);
        password=findViewById(R.id.password_editText_id);
        confirmPassword=findViewById(R.id.confirmPassword_editText_id);
        passMatched=findViewById(R.id.passMatchedIcon_id);
        passNotMatched=findViewById(R.id.passNotMatchedIcon_id);
        passVisibility=findViewById(R.id.passVisibility_id);
        login=findViewById(R.id.logintext_id);
        checkBox=findViewById(R.id.checkbox_id);
        registration=findViewById(R.id.reg_button_id);

        //login intent
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        //password toggle
        passVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationPasswordToggle();
            }
        });

        //registration
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRegistrationInputs();
            }
        });

        //terms intent
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToTermsActivity();
            }
        });

        //calling the login text watcher method
        password.addTextChangedListener(loginTextWatcher);
        confirmPassword.addTextChangedListener(loginTextWatcher);

        //registration button enabled after checkbox marked
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String reg_emailInput=reg_email.getText().toString().trim();
                String passwordInput=password.getText().toString().trim();
                String confirmPassInput=confirmPassword.getText().toString().trim();

                if (isChecked){
                    flag = true;
                    registration.setEnabled(!reg_emailInput.isEmpty() && !passwordInput.isEmpty() && !confirmPassInput.isEmpty() && flag);
                }else {
                    registration.setEnabled(false);
                    flag = false;
                }
                flag = false;
            }
        });
    }

    //method for sending the user to terms and conditions activity
    private void sendUserToTermsActivity() {
        Intent intent=new Intent(Registration.this, Terms.class);
        startActivity(intent);
    }

    //password toggle method
    private void registrationPasswordToggle() {

        if (isShowPassword) {
            password.setTransformationMethod(new PasswordTransformationMethod());
            passVisibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_password));
            isShowPassword = false;
        }else{
            password.setTransformationMethod(null);
            passVisibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
            isShowPassword = true;
        }
    }

    //registration inputs validation inputs
    private void validateRegistrationInputs() {
        String emailInput = reg_email.getText().toString().trim();
        String passwordInput=password.getText().toString().trim();
        String confirmPassInput=confirmPassword.getText().toString().trim();

        if(emailInput.isEmpty()){
            reg_email.setError("Email field can't be empty");
            reg_email.requestFocus();
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            //shake animation
            Animation animShake = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
            reg_email.startAnimation(animShake);

            reg_email.setError("Invalid Email");
            reg_email.requestFocus();
            return;

        } else if (passwordInput.isEmpty()){
            //shake animation
            Animation animShake = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
            password.startAnimation(animShake);

            password.setError("Password can't be empty");
            password.requestFocus();
            return;
        }else if (confirmPassInput.isEmpty()){
            //shake animation
            Animation animShake = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
            confirmPassword.startAnimation(animShake);

            confirmPassword.setError("Confirm Password is empty");
            confirmPassword.requestFocus();
            return;
        }else if (passwordInput.length()<6){
            //shake animation
            Animation animShake = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
            password.startAnimation(animShake);

            password.setError("Password should be 6-12 characters long");
            password.requestFocus();
            return;
        }else if (!passwordInput.equals(confirmPassInput)){
            Animation animShake = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
            confirmPassword.startAnimation(animShake);
            confirmPassword.setError("Password don't match");
            confirmPassword.requestFocus();
            return;
        }else {
            //registration method
            registerUser(emailInput,passwordInput);
        }
    }

    //registration method
    private void registerUser(String emailInput, String passwordInput) {
        //showing the progress loading bar
        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Wait for a moment while we are connecting with you");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        mAuth.createUserWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loadingBar.dismiss();
                    FirebaseUser user=mAuth.getCurrentUser();
                    //get user email and uid from auth
                    String email=user.getEmail();
                    String uid=user.getUid();
                    //when user is registered store user info in fireBase realtime database too
                    //using hashMap
                    HashMap<Object, String>hashMap=new HashMap<>();
                    //put info in hashMap
                    hashMap.put("email",email);
                    hashMap.put("uid",uid);
                    //firebase database instance
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    //database reference path
                    DatabaseReference reference=database.getReference("Users");
                    //put data within hashMap in database
                    reference.child(uid).setValue(hashMap);

                    Toast.makeText(Registration.this, "Registered Successfully..\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                    SendUserToViewPagerActivity();
                    finish();
                }else {
                    String message=task.getException().getMessage();
                    Toast.makeText(Registration.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registration.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
    }

    //method for sending the user to the login activity
    private void sendUserToLoginActivity() {
        Intent intent=new Intent(Registration.this, Login.class);
        startActivity(intent);
    }

    /*method for login text watcher*/
    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String passwordInput=password.getText().toString().trim();
            String confirmPassInput=confirmPassword.getText().toString().trim();

            if (!passwordInput.isEmpty()){
                passVisibility.setVisibility(View.VISIBLE);
            }else if (passwordInput.isEmpty()){
                passVisibility.setVisibility(View.GONE);
            }

            if (passwordInput.equals(confirmPassInput)){
                passMatched.setVisibility(View.VISIBLE);
                passNotMatched.setVisibility(View.INVISIBLE);
            }else if (!passwordInput.equals(confirmPassInput)){

                passMatched.setVisibility(View.INVISIBLE);
                passNotMatched.setVisibility(View.VISIBLE);

            }



        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //method for sending the user to view pager activity after successful registration
    private void SendUserToViewPagerActivity() {

        Intent intent=new Intent(Registration.this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }
}
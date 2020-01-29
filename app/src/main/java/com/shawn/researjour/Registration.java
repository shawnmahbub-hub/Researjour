package com.shawn.researjour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    /*declaring variables for the components
    inside the registration activity
     */
    ImageButton back_button,google,fb,phone;
    EditText reg_email, password, confirmPassword;
    Button registration;
    TextView login,terms;
    CheckBox checkBox;
    boolean flag;
    ProgressDialog loadingBar;

    //initiating firebaseauthencating for registering user
    FirebaseAuth mAuth;
    //DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        //userReference=FirebaseDatabase.getInstance().getReference();
        loadingBar=new ProgressDialog(this);

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

        reg_email=findViewById(R.id.reg_email_editText_id);
        terms=findViewById(R.id.Terms_id);
        password=findViewById(R.id.password_editText_id);
        confirmPassword=findViewById(R.id.confirmPassword_editText_id);

        checkBox=findViewById(R.id.checkbox_id);

        registration=findViewById(R.id.reg_button_id);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailInput = reg_email.getText().toString().trim();
                String passwordInput=password.getText().toString().trim();
                String confirmPassInput=confirmPassword.getText().toString().trim();
                //FirebaseUser currentUser=mAuth.getCurrentUser();

                if(emailInput.isEmpty()){
                    reg_email.setError("Email field can't be empty");
                    reg_email.requestFocus();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                    reg_email.setError("Invalid Email");
                    reg_email.requestFocus();
                    return;

                } else if (passwordInput.isEmpty()){
                    password.setError("Password can't be empty");
                    password.requestFocus();
                    return;
                }else if (confirmPassInput.isEmpty()){
                    confirmPassword.setError("Confirm Password is empty");
                    confirmPassword.requestFocus();
                    return;
                }else if (passwordInput.length()<6){
                    password.setError("Password should be 6-12 characters long");
                    password.requestFocus();
                    return;
                }else if (!passwordInput.equals(confirmPassInput)){
                    confirmPassword.setError("Password don't match");
                    confirmPassword.requestFocus();
                    return;
                }else {
                    //showing the progress loading bar
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Wait for a moment while we are connecting with you");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    if (!isNetworkConnected()==true){
                        Toast.makeText(Registration.this, "No Connection", Toast.LENGTH_SHORT).show();
                    }

                    mAuth.createUserWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                //calling the method
                                SendUserToViewPagerActivity();
                                Toast.makeText(Registration.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else {
                                String message=task.getException().getMessage();
                                Toast.makeText(Registration.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
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

    private void SendUserToViewPagerActivity() {

        Intent intent=new Intent(Registration.this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Registration.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() !=null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

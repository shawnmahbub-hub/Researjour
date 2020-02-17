package com.shawn.researjour.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shawn.researjour.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText email, password;
    ImageButton loginPassVisibility;
    TextView forgot_pass, sign_up;
    Button login;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    private boolean isShowPassword;
    //DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*finding the id of the declared variables */
        forgot_pass=findViewById(R.id.forgot_pass_id);
        sign_up=findViewById(R.id.sign_up_text_id);
        login=findViewById(R.id.login_button_id);
        email=findViewById(R.id.email_editText_id);
        password=findViewById(R.id.password_editText_id);

        loginPassVisibility=findViewById(R.id.login_passVisibility_id);
        loginPassVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPassword) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    loginPassVisibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_password));
                    isShowPassword = false;
                }else{
                    password.setTransformationMethod(null);
                    loginPassVisibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
                    isShowPassword = true;
                }
            }
        });

        mAuth=FirebaseAuth.getInstance();
        //userReference= FirebaseDatabase.getInstance().getReference();
        loadingBar=new ProgressDialog(this);

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
                String emailInput = email.getText().toString().trim();
                String passwordInput=password.getText().toString().trim();
                //FirebaseUser currentUser=mAuth.getCurrentUser();

                if(emailInput.isEmpty()){

                    Animation animShake = AnimationUtils.loadAnimation(Login.this, R.anim.shake);
                    email.startAnimation(animShake);
                    email.setError("Email can't be empty");
                    email.requestFocus();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                    Animation animShake = AnimationUtils.loadAnimation(Login.this, R.anim.shake);
                    email.startAnimation(animShake);
                    email.setError("Invalid Email");
                    email.requestFocus();
                    return;

                } else if (passwordInput.isEmpty()){
                    password.setError("Password can't be empty");
                    password.requestFocus();
                    return;
                }else {
                    //loading bar
                    loadingBar.setTitle("Logging in");
                    loadingBar.setMessage("wait for a moment, while you logged in");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    if (!isNetworkConnected()==true){
                        
                        Toast.makeText(Login.this, "No Connection", Toast.LENGTH_SHORT).show();
                    }

                    mAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendUserToHomeActivity();
                                Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else {
                                String message=task.getException().getMessage();
                                Animation animShake = AnimationUtils.loadAnimation(Login.this, R.anim.shake);
                                password.startAnimation(animShake);
                                Toast.makeText(Login.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }

            }
        });
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Registration.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() !=null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    //method for sending the user to feed activity
    private void sendUserToHomeActivity() {
        Intent intent=new Intent(Login.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(Login.this, "Feed Activity", Toast.LENGTH_SHORT).show();
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

            if (!passwordInput.isEmpty()){
                loginPassVisibility.setVisibility(View.VISIBLE);
            }else if (passwordInput.isEmpty()){
                loginPassVisibility .setVisibility(View.GONE);
            }

            //setting the button enabled if the edit text field is not empty
            login.setEnabled(!userNameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}

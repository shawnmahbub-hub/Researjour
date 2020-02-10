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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shawn.researjour.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    /*declaring variables for the components
    inside the registration activity
     */
    ImageView passMatched,passNotMatched;
    ImageButton google,fb,phone, passVisibility;
    EditText reg_email, password, confirmPassword;
    Button registration;
    TextView login,terms;
    CheckBox checkBox;
    boolean flag;
    ProgressDialog loadingBar;

    //initiating firebaseauthencating for registering user
    FirebaseAuth mAuth;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean isShowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

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
        passMatched=findViewById(R.id.passMatchedIcon_id);
        passNotMatched=findViewById(R.id.passNotMatchedIcon_id);
        passVisibility=findViewById(R.id.passVisibility_id);

        password.addTextChangedListener(loginTextWatcher);

        passVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


        password.addTextChangedListener(loginTextWatcher);
        confirmPassword.addTextChangedListener(loginTextWatcher);

        checkBox=findViewById(R.id.checkbox_id);


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

                    if (!isNetworkConnected()){
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

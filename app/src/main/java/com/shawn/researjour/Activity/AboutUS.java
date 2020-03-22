package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_u_s);

        //calling checkUserStatus method
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            //user is signed in
            String myUid = user.getUid();
        }else {
            //user not signed in, go to login activity
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
}

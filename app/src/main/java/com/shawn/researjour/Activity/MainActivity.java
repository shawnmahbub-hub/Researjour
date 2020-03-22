package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this, Welcome_Screen.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        },5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus();

    }

    private void checkUserStatus(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            //user is signed in
            myUid=user.getUid();
            sendUserToHomeActivity();
        }else {
            //user not signed in, go to login activity
            startActivity(new Intent(this,Login.class));
            finish();
        }
    }

    private void sendUserToHomeActivity() {
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
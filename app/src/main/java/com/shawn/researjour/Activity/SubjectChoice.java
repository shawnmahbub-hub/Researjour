package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shawn.researjour.R;

import androidx.appcompat.app.AppCompatActivity;

public class SubjectChoice extends AppCompatActivity {

    Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_choice);

        finishButton=(Button) findViewById(R.id.sub_finish_button_id);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHomeActivity();
            }
        });
    }

    private void sendUserToHomeActivity() {

        Intent intent=new Intent(this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

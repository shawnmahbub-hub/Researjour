package com.shawn.researjour;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class ProfileSetup extends AppCompatActivity {

    CircleImageView imagePicker;
    EditText fullName,dob_picker;
    Button next;

    String[] language ={"C","C++","Java",".NET","iPhone","Android","ASP.NET","PHP"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        imagePicker=findViewById(R.id.image_picker);
        fullName=findViewById(R.id.full_name_id);
        dob_picker=findViewById(R.id.dobText_id);
        next= findViewById(R.id.profile_next_button_id);

        //calender instance for picking the date of birth
        Calendar mCalender=Calendar.getInstance();
        final int year=mCalender.get(Calendar.YEAR);
        final int month=mCalender.get(Calendar.MONTH);
        final int day=mCalender.get(Calendar.DAY_OF_MONTH);

        dob_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(ProfileSetup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                       month=month+1;
                       String date=day+"/"+month+"/"+year;
                       dob_picker.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToCategorySelection();
            }
        });

        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,language);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv =  (AutoCompleteTextView)findViewById(R.id.universityText_id);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

    }

    private void sendUserToCategorySelection() {
        Intent intent=new Intent(ProfileSetup.this,CategorySelection.class);
        startActivity(intent);
        Toast.makeText(this, "Category Selection", Toast.LENGTH_SHORT).show();
    }
}

package com.shawn.researjour.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shawn.researjour.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class AddNewPost extends AppCompatActivity {

    Toolbar newPostToolbar;

    private EditText researchTitle,abstraction;
    ImageView showImageArea;
    private ImageButton addPostImage;
    private Button postButton;
    private static final int REQUESCODE=1;
    private Uri imageURI;

    private String saveCurrentDate, saveCurrentTime,postRandomName;

    private StorageReference postImageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        postImageReference= FirebaseStorage.getInstance().getReference();

        //finding the id of the widgets in the add post activity
        researchTitle=(EditText) findViewById(R.id.postTitleText_id);
        abstraction=(EditText)findViewById(R.id.postabstractionText_id);
        addPostImage=(ImageButton)findViewById(R.id.addPostImage_id);
        postButton=(Button)findViewById(R.id.post_button_id);
        showImageArea=(ImageView)findViewById(R.id.showImageArea_id);

        addPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInputs();
            }
        });


        newPostToolbar = findViewById(R.id.addNewPostPageToolbar);
        setSupportActionBar(newPostToolbar);

        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add New Research");
    }

    private void validatePostInputs() {

        String titleInput=researchTitle.getText().toString();
        String abstractionInput=abstraction.getText().toString();

        if (TextUtils.isEmpty(titleInput)){

            Toast.makeText(this, "Title is blank", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(abstractionInput)){
            Toast.makeText(this, "Abstraction is blank", Toast.LENGTH_SHORT).show();
        }else if (imageURI==null){
            Toast.makeText(this, "Please select image for your research", Toast.LENGTH_SHORT).show();
        }else {

            savePostImageToFirebaseStorage();
        }

    }

    private void savePostImageToFirebaseStorage() {

        /*update the time and day at which time the user
        update the post*/

        Calendar callForDate=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("dd-MM-YYYY");
        saveCurrentDate=currentDate.format(callForDate.getTime());


        Calendar callFortime=Calendar.getInstance();
        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(callFortime.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;

        StorageReference filePath=postImageReference.child("Post Images")
                .child(imageURI.getLastPathSegment()+ postRandomName+".jpg");

        filePath.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(AddNewPost.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                }else {

                    String message=task.getException().getMessage();
                    Toast.makeText(AddNewPost.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUESCODE && resultCode==RESULT_OK && data!=null){
            imageURI=data.getData();
            showImageArea.setImageURI(imageURI);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home){
            sendUserToHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToHomeActivity() {

        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}

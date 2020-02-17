package com.shawn.researjour.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;


public class AddNewPost extends AppCompatActivity {

    //components part
    Toolbar newPostToolbar;
    private CircleImageView postProfilePicture;
    private TextView postUserName, postTime,postButton;
    private EditText researchTitle,abstraction;
    private ImageView showImageArea;
    private ImageButton addPostImage;
    private Uri imageURI=null;
    //Permissions constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    //permission array
    String[] cameraPermissions;
    String[] storagePermissions;

    //get some info of the user
    String name,email,uid;

    private ProgressDialog loadingBar;

    //firebase part
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        //init permissions arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //firebase part
        mAuth= FirebaseAuth.getInstance();
        checkUserStatus();
        //get some info of current user to include in post
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query=UsersRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    name=""+dataSnapshot1.child("name").getValue();
                    email=""+dataSnapshot1.child("email").getValue();
                }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadingBar=new ProgressDialog(this);

        //finding the id of the widgets in the add post activity
        postProfilePicture=(CircleImageView)findViewById(R.id.postProfileImage_id);
        postUserName=(TextView)findViewById(R.id.postProfileName_id);
        postTime=(TextView)findViewById(R.id.postTimeText_id);
        researchTitle=(EditText) findViewById(R.id.postTitleText_id);
        abstraction=(EditText)findViewById(R.id.postabstractionText_id);
        addPostImage=(ImageButton)findViewById(R.id.addPostImage_id);
        postButton=(TextView) findViewById(R.id.post_button_id);
        showImageArea=(ImageView)findViewById(R.id.showImageArea_id);

        String currentUserID = mAuth.getCurrentUser().getUid();
        /*fetching the user profile image and user name
                from firebase*/
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {


                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        postUserName.setText(fullname);

                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(AddNewPost.this).load(image).placeholder(R.drawable.profile_image).into(postProfilePicture);
                    }
                    if (dataSnapshot.hasChild("university")){
                        String universityName = dataSnapshot.child("university").getValue().toString();
                        postTime.setText(universityName);
                    }
                    else
                    {
                        Toast.makeText(AddNewPost.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //open gallery when user wanted to post picture
        addPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        //button for posting the new research information to the firebase
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get data of research title and abstraction from edit text
                String researchTitleInput=researchTitle.getText().toString().trim();
                String abstractionInput=abstraction.getText().toString().trim();

                if (TextUtils.isEmpty(researchTitleInput)){
                    Animation animShake = AnimationUtils.loadAnimation(AddNewPost.this, R.anim.shake);
                    researchTitle.startAnimation(animShake);
                    Toast.makeText(AddNewPost.this, "Enter Title..", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(abstractionInput)){
                    Animation animShake = AnimationUtils.loadAnimation(AddNewPost.this, R.anim.shake);
                    abstraction.startAnimation(animShake);
                    Toast.makeText(AddNewPost.this, "Write abstraction..", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageURI==null){
                    //post status without image
                    uploadData(researchTitleInput,abstractionInput,"noImage");

                }else {
                    //post with image
                    uploadData(researchTitleInput,abstractionInput,String.valueOf(imageURI));
                }

            }
        });


        newPostToolbar = findViewById(R.id.addNewPostPageToolbar);
        setSupportActionBar(newPostToolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add New Research");

    }

    private void checkUserStatus() {

        //get current user
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            //user is signed in stay here
            email=user.getEmail();
            uid=user.getUid();

        }else {
            //user not signed in, go to main activity
            startActivity(new Intent(this,Home.class));
            finish();
        }
    }

    private void uploadData(final String researchTitleInput, final String abstractionInput, final String uri) {

        loadingBar.setTitle("Publishing Post..");
        loadingBar.setMessage("Please wait, while we are publishing your new post...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        //for post-image name, post-id, post-publish time
        final String timeStamp=String.valueOf(System.currentTimeMillis());

        //setting a unique name for the post
        String filePathAndName="Posts/"+"Post_"+timeStamp;

        if (!uri.equals("noImage")){
            //post with image
            StorageReference reference=FirebaseStorage.getInstance().getReference().child(filePathAndName);
            reference.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    String downloadUri=uriTask.getResult().toString();

                    if (uriTask.isSuccessful()){
                        //uri is received upload post to firebase database
                        HashMap<Object,String> hashMap=new HashMap<>();
                        //put post info
                        hashMap.put("uid", uid);
                        hashMap.put("title", researchTitleInput);
                        hashMap.put("abstraction", abstractionInput);
                        hashMap.put("postimage", downloadUri);
                        hashMap.put("pTime",timeStamp);
                        hashMap.put("uName",name);

                        //path to store post data
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Posts");

                        //put data in this database referece
                        databaseReference.child(timeStamp).setValue(hashMap).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //added in database
                                        loadingBar.dismiss();
                                        Toast.makeText(AddNewPost.this, "Post Published", Toast.LENGTH_SHORT).show();

                                        //reset views
                                        researchTitle.setText("");
                                        abstraction.setText("");
                                        showImageArea.setImageURI(null);
                                        imageURI=null;

                                        //sending the user to home activity
                                        sendUserToHomeActivity();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            //post without image
            ////uri is recieved upload post to firebase database
            HashMap<Object,String> hashMap=new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("title", researchTitleInput);
            hashMap.put("abstraction", abstractionInput);
            hashMap.put("postimage", "noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("uName",name);

            //path to store post data
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Posts");

            //put data in this database reference
            databaseReference.child(timeStamp).setValue(hashMap).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            loadingBar.dismiss();
                            Toast.makeText(AddNewPost.this, "Post Published", Toast.LENGTH_SHORT).show();
                            //reset views
                            researchTitle.setText("");
                            abstraction.setText("");
                            showImageArea.setImageURI(null);
                            imageURI=null;

                            //sending the user to home activity
                            sendUserToHomeActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    //method for showing the alert dialog
    private void showImagePickDialog() {

        String[] options={"Camera", "Gallery"};

        //dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");

        //setting options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handler for item click
                if (which==0){
                    //camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                }
                if (which==1){
                    //gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void pickFromGallery() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        //intent to pick image from camera

        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        imageURI=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    //4 Methods for camera and gallery storage permission
    private boolean checkStoragePermission(){

        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission
        .WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        //check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission
                .CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission
                .WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
    }



    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /*this method is called when user press Allow
        * or Deny from permission request dialog and here
        * we will handle permission cases is allowed or denied*/

        switch (requestCode){

            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();

                    }else {
                        Toast.makeText(this, "Camera permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){

                        //storage permission are granted
                        pickFromGallery();

                    }else {

                        Toast.makeText(this, "Storage permission necessary", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
        }
    }

    //saving the selected image to the show image area
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            if (requestCode==IMAGE_PICK_GALLERY_CODE){

                //image is picked form gallery, get uri of imamge
                imageURI=data.getData();

                //set to image view area
                showImageArea.setImageURI(imageURI);

            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
                showImageArea.setImageURI(imageURI);
            }
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

    //intent for sending the user to the home activity when click the back button
    private void sendUserToHomeActivity()  {

        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}
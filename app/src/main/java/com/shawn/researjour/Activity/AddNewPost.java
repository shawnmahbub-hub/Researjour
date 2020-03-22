package com.shawn.researjour.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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
    private TextView postUserName, postTime,postButton,pdfName;
    private EditText researchTitle,abstraction,videoLink;
    private ImageView showImageArea;
    private ImageButton addPostImage,addPdf;
    private Uri imageURI,pdfUri;

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
    String name,email,uid,dp;

    //info of post ot be edited
    String editTitle,editAbstraction,editImage;

    //firebase part
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        //finding the id of the widgets in the add post activity
        postProfilePicture=(CircleImageView)findViewById(R.id.postProfileImage_id);
        postUserName=(TextView)findViewById(R.id.postProfileName_id);
        postTime=(TextView)findViewById(R.id.postTimeText_id);
        researchTitle=(EditText) findViewById(R.id.postTitleText_id);
        abstraction=(EditText)findViewById(R.id.postabstractionText_id);
        videoLink=(EditText)findViewById(R.id.videoLinkET_id);
        addPostImage=(ImageButton)findViewById(R.id.addPostImage_id);
        addPdf=(ImageButton)findViewById(R.id.addPdf_id);
        pdfName=findViewById(R.id.pdfTextView_id);
        postButton=(TextView) findViewById(R.id.post_button_id);
        showImageArea=(ImageView)findViewById(R.id.showImageArea_id);

        //init permissions arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //firebase part
        mAuth= FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();

        checkUserStatus();

        //get data through intent from previous activities adapter
        Intent intent=getIntent();
        String isUpdateKey=""+intent.getStringExtra("key");
        String editPostId=""+intent.getStringExtra("editPostId");
        //validate if we came here to update post i.e. came fro PostAdapter
        if (isUpdateKey.equals("editPost")){
            //action bar
            newPostToolbar = findViewById(R.id.addNewPostPageToolbar);
            setSupportActionBar(newPostToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Update Research Post");

            postButton.setText("Update");
            loadPostData(editPostId);
        }else {
            //add new post
            //action bar
            newPostToolbar = findViewById(R.id.addNewPostPageToolbar);
            setSupportActionBar(newPostToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Add New Research");
            postButton.setText("Post");
        }

        //get some info of current user to include in post
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query=UsersRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    name=""+dataSnapshot1.child("fullname").getValue();
                    email=""+dataSnapshot1.child("email").getValue();
                    dp=""+dataSnapshot1.child("profileimage").getValue();
                }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String currentUserID = mAuth.getCurrentUser().getUid();
        /*fetching the user profile image and user name from fireBase*/
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
                        Picasso.get().load(image).placeholder(R.drawable.user_profile).into(postProfilePicture);
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


        //open file storage when user wanted to post pdf file
        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission
                        (AddNewPost.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED)
                {
                    selectPdf();
                }else {
                    ActivityCompat.requestPermissions(AddNewPost.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        //button for posting the new research information to the fireBase
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInputs();
            }
        });
    }


    private void loadPostData(String editPostId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        //get details of post using id of post
        Query fquery=reference.orderByChild("postid").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    editTitle=""+ds.child("title").getValue();
                    editAbstraction=""+ds.child("abstraction").getValue();
                    editImage=""+ds.child("postimage").getValue();

                    //set data to views
                    researchTitle.setText(editTitle);
                    abstraction.setText(editAbstraction);
                    //set image
                    if (!editImage.equals("noImage")){
                        try {
                            Picasso.get().load(editImage).into(showImageArea);
                        }catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //method for selecting the pdf file from storage
    private void selectPdf() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    //validate post input method
    private void validatePostInputs() {
        //get data of research title and abstraction from edit text
        String researchTitleInput=researchTitle.getText().toString().trim();
        String abstractionInput=abstraction.getText().toString().trim();
        String VideoLinkInput=videoLink.getText().toString().trim();
        Intent intent=getIntent();
        String isUpdateKey=""+intent.getStringExtra("key");
        String editPostId=""+intent.getStringExtra("editPostId");

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
        }else if (TextUtils.isEmpty(VideoLinkInput)){
            Animation animShake = AnimationUtils.loadAnimation(AddNewPost.this, R.anim.shake);
            videoLink.startAnimation(animShake);
            Toast.makeText(AddNewPost.this, "Please upload your project video to YouTube First, then paste the link here", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isUpdateKey.equals("editPost")){
            beginUpdate(researchTitleInput,abstractionInput,VideoLinkInput,editPostId);
        }else {
            //pdf file
            if (pdfUri!=null){
                uploadFile(pdfUri);
            }
            uploadData(researchTitleInput,abstractionInput,VideoLinkInput);
        }
    }

    //pdf upload file
    private void uploadFile(Uri pdfUri) {

        final String pdftimeStamp=String.valueOf(System.currentTimeMillis());

        final String fileName="pdf_"+pdftimeStamp;
        StorageReference storageReference=storage.getReference();

        storageReference.child("pdfUploads").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        DatabaseReference databaseReference=database.getReference();

                        databaseReference.child("pdf").child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AddNewPost.this, "your research pdf uploaded successfully", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(AddNewPost.this, "your research pdf not uploaded successfully", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewPost.this, "your research pdf not uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //upload data to fireBase Method
    private void uploadData(final String researchTitleInput, final String abstractionInput,final String VideoLinkInput) {
        //progress dialog
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading Research Paper..");
        progressDialog.setProgress(0);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //for post-image name, post-id, post-publish time
        final String timeStamp=String.valueOf(System.currentTimeMillis());

        //setting a unique name for the post
        String filePathAndName="Posts/"+"Post_"+timeStamp;

        if (showImageArea.getDrawable()!=null){

            //get image from show image area
            Bitmap bitmap=((BitmapDrawable)showImageArea.getDrawable()).getBitmap();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();

            //image compress
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] data=baos.toByteArray();
            //post with image
            StorageReference reference=FirebaseStorage.getInstance().getReference().child(filePathAndName);
            reference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    String downloadUri=uriTask.getResult().toString();
                    String pLikes= String.valueOf(0);
                    String pComments= String.valueOf(0);

                    if (uriTask.isSuccessful()){
                        //uri is received upload post to firebase database
                        HashMap<String, Object> hashMap=new HashMap<>();
                        //put post info
                        hashMap.put("uid", uid);
                        hashMap.put("uName",name);
                        hashMap.put("uDp",dp);
                        hashMap.put("postid",timeStamp);
                        hashMap.put("pLikes",pLikes);
                        hashMap.put("pComments",pComments);
                        hashMap.put("uEmail",email);
                        hashMap.put("title", researchTitleInput);
                        hashMap.put("abstraction", abstractionInput);
                        hashMap.put("videoLink", VideoLinkInput);
                        hashMap.put("postimage", downloadUri);
                        hashMap.put("pTime",timeStamp);

                        //path to store post data
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Posts");

                        //put data in this database reference
                        databaseReference.child(timeStamp).setValue(hashMap).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //added in database
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
                                Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    //track the progress of our pdf upload
                    int currentProgress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currentProgress);
                }
            });

        }else {
            //post without image
            //uri is received upload post to firebase database
            HashMap<Object,String> hashMap=new HashMap<>();
            String pLikes= String.valueOf(0);
            String pComments= String.valueOf(0);

            //put post info
            hashMap.put("uid", uid);
            hashMap.put("uName",name);
            hashMap.put("uDp",dp);
            hashMap.put("postid",timeStamp);
            hashMap.put("pLikes",pLikes);
            hashMap.put("pComments",pComments);
            hashMap.put("uEmail",email);
            hashMap.put("title", researchTitleInput);
            hashMap.put("abstraction", abstractionInput);
            hashMap.put("videoLink", VideoLinkInput);
            hashMap.put("postimage", "noImage");
            hashMap.put("pTime",timeStamp);

            //path to store post data
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Posts");

            //put data in this database reference
            databaseReference.child(timeStamp).setValue(hashMap).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
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
                    Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    //beginning the update
    private void beginUpdate(String researchTitleInput, String abstractionInput, String VideoLinkInput,String editPostId) {
        if (!editImage.equals("noImage")){
            //with Image
            updateWasWithImage(researchTitleInput,abstractionInput,VideoLinkInput,editPostId);
        }else if (showImageArea.getDrawable() !=null){
            //without image
            updateWithNowImage(researchTitleInput,abstractionInput,VideoLinkInput,editPostId);
        }else {
            //without image
            updateWithoutImage(researchTitleInput,abstractionInput,VideoLinkInput,editPostId);
        }
    }

    private void updateWithoutImage(String researchTitleInput, String abstractionInput,String VideoLinkInput, String editPostId) {
        //progress dialog
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Updating Research Paper..");
        progressDialog.setProgress(0);
        progressDialog.show();

        //uri is received upload post to firebase database
        HashMap<String, Object> uphashMap=new HashMap<>();
        //put post info
        uphashMap.put("uid", uid);
        uphashMap.put("uName",name);
        uphashMap.put("uEmail",email);
        uphashMap.put("uDp",dp);
        uphashMap.put("title", researchTitleInput);
        uphashMap.put("abstraction", abstractionInput);
        uphashMap.put("videoLink", VideoLinkInput);
        uphashMap.put("postimage", "noImage");

        //path to store post data
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(editPostId).updateChildren(uphashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(AddNewPost.this, "Research Post Updated..", Toast.LENGTH_SHORT).show();
                        sendUserToHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddNewPost.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWithNowImage(final String researchTitleInput, final String abstractionInput, final String VideoLinkInput,final String editPostId) {
        //progress dialog
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Updating Research Paper..");
        progressDialog.setProgress(0);
        progressDialog.show();

        String timeStamp=String.valueOf(System.currentTimeMillis());
        String filePathAndName="Posts/"+"Post_"+timeStamp;

        //get image from show image area
        Bitmap bitmap=((BitmapDrawable)showImageArea.getDrawable()).getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data=baos.toByteArray();

        StorageReference ref=FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloadUri=uriTask.getResult().toString();

                if (uriTask.isSuccessful()){

                    //uri is received upload post to firebase database
                    HashMap<String, Object> uphashMap=new HashMap<>();
                    //put post info
                    uphashMap.put("uid", uid);
                    uphashMap.put("uName",name);
                    uphashMap.put("uEmail",email);
                    uphashMap.put("uDp",dp);
                    uphashMap.put("title", researchTitleInput);
                    uphashMap.put("abstraction", abstractionInput);
                    uphashMap.put("videoLink", VideoLinkInput);
                    uphashMap.put("postimage", downloadUri);

                    //path to store post data
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                    ref.child(editPostId).updateChildren(uphashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    sendUserToHomeActivity();
                                    Toast.makeText(AddNewPost.this, "Research Post Updated..", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewPost.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                //track the progress of our pdf upload
                int currentProgress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    private void updateWasWithImage(final String researchTitleInput, final String abstractionInput,final String VideoLinkInput, final String editPostId) {
        //progress dialog
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Updating Research Paper..");
        progressDialog.setProgress(0);
        progressDialog.show();

        //post was with image, delete previous image first
        StorageReference mPictureRef=FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //image deleted, upload new image
                /*for post-image name, post-id, publish-time*/
                String timeStamp=String.valueOf(System.currentTimeMillis());
                String filePathAndName="Posts/"+"Post_"+timeStamp;

                //get image from show image area
                Bitmap bitmap=((BitmapDrawable)showImageArea.getDrawable()).getBitmap();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();

                //image compress
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[] data=baos.toByteArray();

                StorageReference ref=FirebaseStorage.getInstance().getReference().child(filePathAndName);
                ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri=uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            //uri is received upload post to firebase database
                            HashMap<String, Object> uphashMap=new HashMap<>();
                            //put post info
                            uphashMap.put("uid", uid);
                            uphashMap.put("uName",name);
                            uphashMap.put("uEmail",email);
                            uphashMap.put("uDp",dp);
                            uphashMap.put("title", researchTitleInput);
                            uphashMap.put("abstraction", abstractionInput);
                            uphashMap.put("videoLink", VideoLinkInput);
                            uphashMap.put("postimage", downloadUri);

                            //path to store post data
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                            ref.child(editPostId).updateChildren(uphashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            sendUserToHomeActivity();
                                            Toast.makeText(AddNewPost.this, "Research Post Updated..", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddNewPost.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        //track the progress of our pdf upload
                        int currentProgress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddNewPost.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    //user status method
    private void checkUserStatus() {

        //get current user
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            //user is signed in stay here
            email=user.getEmail();
            uid=user.getUid();

        }else {
            //user not signed in, go to main activity
            startActivity(new Intent(this,Login.class));
            finish();
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

    //saving the selected image to the show image area
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //check whether user has selected a pdf file or not
        if (requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            pdfUri=data.getData();
            pdfName.setText("file: "+data.getData().getLastPathSegment());
        }

        if (resultCode==RESULT_OK){

            if (requestCode==IMAGE_PICK_GALLERY_CODE){

                //image is picked form gallery, get uri of image
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

    //4 Methods for camera and gallery storage permission
    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
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

        if (requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }
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
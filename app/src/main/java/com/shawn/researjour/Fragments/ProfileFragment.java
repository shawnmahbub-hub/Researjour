package com.shawn.researjour.Fragments;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.shawn.researjour.Activity.AboutUS;
import com.shawn.researjour.Activity.Welcome_Screen;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Toolbar mTopToolbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath="UsersProfileCoverImages/";
    private StorageReference UserProfileImageRef;
    private String currentUserID;

    //views from xml
    ImageView coverIv;
    CircleImageView profileImageView;
    TextView userName,uEmail,researcherRole,university,gender,logoutBtn,aboutApp;
    FloatingActionButton fab;
    private Uri imageURI;
    String uid;

    //fore checking profile or cover image
    String profileOrCoverImage;

    //progress dialog
    ProgressDialog pd;

    //Permissions constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    //permission array
    String[] cameraPermissions;
    String[] storagePermissions;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_profile,container,false);

        //init fireBase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference= getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        //init views from xml components
        coverIv=view.findViewById(R.id.coverIv_id);
        profileImageView=view.findViewById(R.id.profileFragment_Picture_id);
        userName=view.findViewById(R.id.profileFragment_username_id);
        uEmail=view.findViewById(R.id.emailTv_id);
        university=view.findViewById(R.id.profileUniversityText_id);
        researcherRole=view.findViewById(R.id.profileRoleText_id);
        gender=view.findViewById(R.id.profileGenderText_id);
        logoutBtn=view.findViewById(R.id.logout_button_id);
        aboutApp=view.findViewById(R.id.about_button_id);
        fab=view.findViewById(R.id.fab_edit_id);

        //init permissions arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //init progress dialog
        pd=new ProgressDialog(getActivity());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the progress loading bar
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), Welcome_Screen.class));
            }
        });

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToAboutUSActivity();
            }
        });

        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    String name=""+ds.child("fullname").getValue();
                    String email=""+ds.child("email").getValue();
                    String userUniversity=""+ds.child("university").getValue();
                    String researcher_Role=""+ds.child("researcherRole").getValue();
                    String uGender=""+ds.child("gender").getValue();
                    String image=""+ds.child("profileimage").getValue();
                    String cover=""+ds.child("cover").getValue();

                    //set data
                    userName.setText(name);
                    uEmail.setText(email);
                    university.setText(userUniversity);
                    researcherRole.setText(researcher_Role);
                    gender.setText(uGender);

                    //profile image
                    if (image!=null){
                        //if image is received then set
                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImageView);
                    }else {
                        Picasso.get().load(R.drawable.profile_image).into(profileImageView);
                    }

                    //cover image
                    try{
                        //if image is received then set
                        Picasso.get().load(cover).into(coverIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.no_cover).into(coverIv);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fab set on click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        return view;
    }

    private void showEditProfileDialog() {
        String options[]={"Cover Image","Researcher Name","University Name"};
        //alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Edit Options");
        //set item to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    //cover image
                    pd.setMessage("Updating Cover Image");
                    profileOrCoverImage="cover";
                    showImagePickerDialog();
                }else if (which==1){
                    //researcher name
                    pd.setMessage("Updating Name");
                    showNameRoleUpdateDialog("fullname");
                }else if (which==2){
                    //researcher name
                    pd.setMessage("Updating University");
                    showNameRoleUpdateDialog("university");
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showNameRoleUpdateDialog(final String key) {
        //custom alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update");
        //set layout of dialog
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText=new EditText(getActivity());
        editText.setHint(key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        //add buttons in dialog
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                final String value=editText.getText().toString().trim();
                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)){

                    pd.show();
                    HashMap<String, Object> result=new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(firebaseUser.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //updated
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    //if user edit his name, also change it from his posts
                    if (key.equals("fullname")){
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                        Query query=ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child= ds.getKey();dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }else {

                    Toast.makeText(getActivity(), "Researcher "+key+"Empty", Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void showImagePickerDialog() {
        String[] options={"Camera", "Gallery"};

        //dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
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

    //4 Methods for camera and gallery storage permission
    private void requestStoragePermission(){
        //request runtime storage permission
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission(){

        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestCameraPermission(){
        //request runtime storage permission
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){

        //check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission
                .CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission
                .WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
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
        imageURI=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
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
                        Toast.makeText(getActivity(), "Camera permissions are necessary", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(getActivity(), "Storage permission necessary", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
        }
    }

    //saving the selected image to the show image area
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            if (requestCode==IMAGE_PICK_GALLERY_CODE){

                //image is picked form gallery, get uri of image
                imageURI=data.getData();
                uploadProfileCoverPhoto(imageURI);

            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
                uploadProfileCoverPhoto(imageURI);
            }
        }
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        pd.show();

        StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
        //StorageReference storageReference1=storageReference.child(filePath);
        filePath.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        final Uri downloadUri=uriTask.getResult();

                        //check if image is uploaded or not and uri is received
                        if (uriTask.isSuccessful()){
                            //image uploaded
                            HashMap<String, Object> results=new HashMap<>();
                            results.put(profileOrCoverImage,downloadUri.toString());

                            databaseReference.child(firebaseUser.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Image Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Image Update failed", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else {
                            //error
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendUserToAboutUSActivity() {
        Intent intent=new Intent(getActivity(), AboutUS.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mTopToolbar=getActivity().findViewById(R.id.toolbar);
        mTopToolbar.setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
    }
}

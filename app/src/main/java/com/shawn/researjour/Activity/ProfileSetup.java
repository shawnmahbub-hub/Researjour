package com.shawn.researjour.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CircleImageView profileImage;
    private EditText fullName,dob_picker,university;
    private Spinner gender,researcherRole;
    private Button nextBtn;
    private ProgressDialog loadingBar;
    final Calendar myCalendar = Calendar.getInstance();

    //fireBase
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    private String currentUserID;

    //Permissions constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    //permission array
    String[] cameraPermissions;
    String[] storagePermissions;

    private Uri imageURI;
    private String uid,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        //init fireBase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        //finding id
        profileImage=findViewById(R.id.profile_image);
        fullName=findViewById(R.id.full_name_id);
        university=findViewById(R.id.universityText_id);
        researcherRole=findViewById(R.id.roleSpinner_id);
        gender=findViewById(R.id.gender_spinner);
        dob_picker=findViewById(R.id.dobText_id);
        nextBtn= findViewById(R.id.profile_next_button_id);
        loadingBar = new ProgressDialog(this);

        //init permissions arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //array adapter for spinner items
        ArrayAdapter<CharSequence> researcherRoleAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_spinnerItems, android.R.layout.simple_spinner_item);
        researcherRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        researcherRole.setAdapter(researcherRoleAdapter);
        researcherRole.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_spinnerItems, android.R.layout.simple_spinner_item);
        researcherRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);
        gender.setOnItemSelectedListener(this);

        /*login text watcher for empty edit text field*/
        fullName.addTextChangedListener(loginTextWatcher);
        university.addTextChangedListener(loginTextWatcher);
        dob_picker.addTextChangedListener(loginTextWatcher);

        //calling check permission method
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    String image=""+ds.child("profileimage").getValue();

                    //profile image
                    try{
                        //if image is received then set
                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImage);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.profile_image).into(profileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fetching user profile image from fireBase
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImage);
                    }
                    else
                    {
                        Toast.makeText(ProfileSetup.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dob_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileSetup.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //next button intent
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                validateProfileInputs();
            }
        });

    }

    private void validateProfileInputs() {
        //get data of research title and abstraction from edit text
        String fullNameInput=fullName.getText().toString().trim();
        String universityInput=university.getText().toString().trim();


        if (TextUtils.isEmpty(fullNameInput)){
            Animation animShake = AnimationUtils.loadAnimation(ProfileSetup.this, R.anim.shake);
            fullName.startAnimation(animShake);
            Toast.makeText(ProfileSetup.this, "Enter Title..", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(universityInput)){
            Animation animShake = AnimationUtils.loadAnimation(ProfileSetup.this, R.anim.shake);
            university.startAnimation(animShake);
            Toast.makeText(ProfileSetup.this, "Write abstraction..", Toast.LENGTH_SHORT).show();
            return;
        }else {
            SaveAccountSetupInformation();
        }

    }

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



    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob_picker.setText(sdf.format(myCalendar.getTime()));
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

    //on activity result method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_PICK_GALLERY_CODE && resultCode==RESULT_OK && data!=null){

            imageURI=data.getData();

            //image is picked form gallery, get uri of image
            startCrop(imageURI);

        }
        if (requestCode==IMAGE_PICK_CAMERA_CODE){
            //image is picked from camera, get uri of image

            startCrop(imageURI);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUri = uri.toString();
                                        UsersRef.child("profileimage").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Intent selfIntent = new Intent(ProfileSetup.this, ProfileSetup.class);
                                                    startActivity(selfIntent);

                                                    Toast.makeText(ProfileSetup.this, "Profile Image stored successfully...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                                else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(ProfileSetup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }

                                        });

                                    }
                                });
                            }
                        }
                    }
                });

            } else
            {
                Toast.makeText(this, "Error: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void startCrop(Uri imageURI) {
        CropImage.activity(imageURI)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    //spinner item selected listener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String researcherRoleInput = parent.getItemAtPosition(position).toString();
        String genderInput = parent.getItemAtPosition(position).toString();

        HashMap userMap = new HashMap();
        if(parent.getId() == R.id.roleSpinner_id)
        {
            userMap.put("researcherRole", researcherRoleInput);
        }
        else if(parent.getId() == R.id.gender_spinner)
        {
            userMap.put("gender", genderInput);
        }

        UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {
                }
                else
                {
                    String message =  task.getException().getMessage();
                    Toast.makeText(ProfileSetup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //saving account info to fireBase
    private void SaveAccountSetupInformation() {

        String universtiyInput = university.getText().toString();
        String fullnameInput = fullName.getText().toString();
        String dobInput = dob_picker.getText().toString();

        loadingBar.setTitle("Saving Information");
        loadingBar.setMessage("Please wait, while we are creating your new Account...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        HashMap userMap = new HashMap();
        userMap.put("fullname", fullnameInput);
        userMap.put("university", universtiyInput);
        userMap.put("date of birth", dobInput);

        UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {
                    SendUserToCategorySelection();
                    Toast.makeText(ProfileSetup.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
                else
                {
                    String message =  task.getException().getMessage();
                    Toast.makeText(ProfileSetup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
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
            String fullNameInput=fullName.getText().toString().trim();
            String universityInput=university.getText().toString().trim();
            String dob_pickerInput=dob_picker.getText().toString().trim();

            //setting the button enabled if the edit text field is not empty
            nextBtn.setEnabled(!fullNameInput.isEmpty() && !universityInput.isEmpty() && !dob_pickerInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //send user to category selection
    private void SendUserToCategorySelection()
    {
        Intent mainIntent = new Intent(ProfileSetup.this, ChooseCategory.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
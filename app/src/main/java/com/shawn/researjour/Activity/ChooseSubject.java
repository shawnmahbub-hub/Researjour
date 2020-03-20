package com.shawn.researjour.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shawn.researjour.Adapter.SubjectFetchAdapter;
import com.shawn.researjour.Models.SubjectFetchModel;
import com.shawn.researjour.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseSubject extends AppCompatActivity {

    private RecyclerView recyclerViewSubjectChoice;
    private ArrayList<SubjectFetchModel> arrayListSubjectFetch;
    private ArrayList<String> arrayListPass;
    private SubjectFetchAdapter subjectFetchAdapter;
    private ProgressDialog pd;

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceSend;

    Button buttonNextFeed;
    String passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_subject);

        buttonNextFeed = findViewById(R.id.sub_finish_button_id);
        pd=new ProgressDialog(this);

        Bundle bundle = getIntent().getExtras();
        String rcv = bundle.getString("msg");

        recyclerViewSubjectChoice = findViewById(R.id.recyclerViewSubjectChoice);
        recyclerViewSubjectChoice.setLayoutManager(new LinearLayoutManager(this));
        arrayListSubjectFetch = new ArrayList<>();
        subjectFetchAdapter = new SubjectFetchAdapter(this,arrayListSubjectFetch);
        recyclerViewSubjectChoice.setAdapter(subjectFetchAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("catsubdata");
        databaseReferenceSend = FirebaseDatabase.getInstance().getReference("sub");
        Query query = databaseReference.orderByChild("cat").equalTo(rcv);
        query.addListenerForSingleValueEvent(valueEventListener);

        final SubjectFetchModel subjectFetchModel = new SubjectFetchModel();

        buttonNextFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                for (int i = 0; i<SubjectFetchAdapter.subjectModelArrayList.size(); i++){
//                     arrayListPass = new ArrayList<>();
//                    arrayListPass.add(SubjectFetchAdapter.subjectModelArrayList.get(i).sub);
//                }
//                if (subjectFetchAdapter.getSelectedSubject().size()>0){
//                    arrayListSubjectFetch = subjectFetchAdapter.getSelectedSubject();
//                }
               if (subjectFetchAdapter.getSelectedSubject().size()>0){
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i<subjectFetchAdapter.getSelectedSubject().size();i++){
                        stringBuilder.append(subjectFetchAdapter.getSelectedSubject().get(i).sub);
                        stringBuilder.append("-");
                        passText = stringBuilder.toString().trim();
                    }
               }
               Intent intent = new Intent(ChooseSubject.this,Home.class);
//                intent.putExtra("arrayData", arrayListPass);
               intent.putExtra("msg",passText);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               finish();
            }
        });

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            arrayListSubjectFetch.clear();
            if (dataSnapshot.exists()){
                for (DataSnapshot dbQuerySnapShot: dataSnapshot.getChildren()){
                    SubjectFetchModel subjectFetchModel = dbQuerySnapShot.getValue(SubjectFetchModel.class);
                    arrayListSubjectFetch.add(subjectFetchModel);
                }
                subjectFetchAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}

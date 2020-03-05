package com.shawn.researjour.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.shawn.researjour.Models.SubjectFetchModel;
import com.shawn.researjour.R;

import java.util.ArrayList;

public class SubjectFetchAdapter extends RecyclerView.Adapter<SubjectFetchAdapter.SubItemViewHolder> {

    LayoutInflater inflater;
    public static ArrayList<SubjectFetchModel> subjectModelArrayList;
    private Context context;
    private DatabaseReference databaseReferenceSend;
    private int checkedPosition = -1;

    public SubjectFetchAdapter(Context context, ArrayList<SubjectFetchModel> subjectModelArrayList){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.subjectModelArrayList = subjectModelArrayList;
    }


    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.subject_choice_item,parent,false);
        SubItemViewHolder subItemViewHolder = new SubItemViewHolder(view);
        return subItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SubItemViewHolder holder, int position) {

//        databaseReferenceSend = FirebaseDatabase.getInstance().getReference("catsubdata");
        holder.checkBoxSub.setChecked(subjectModelArrayList.get(position).isSelected());
        holder.textViewSub.setText(subjectModelArrayList.get(position).sub);


        holder.checkBoxSub.setTag(position);
        holder.checkBoxSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.checkBoxSub.getTag();
//                String id = databaseReferenceSend.push().getKey();
//                databaseReferenceSend.child("sub").child(id).child("choice").setValue(subjectModelArrayList.get(pos).sub);
                Toast.makeText(context,subjectModelArrayList.get(pos).sub + "clicked" ,+ Toast.LENGTH_SHORT).show();

                if (subjectModelArrayList.get(pos).isSelected()){
                    subjectModelArrayList.get(pos).setSelected(false);
                }
                else {
                    subjectModelArrayList.get(pos).setSelected(true);

                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectModelArrayList.size();
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder{

        protected CheckBox checkBoxSub;
        private TextView textViewSub;
        private DatabaseReference databaseReferenceSend;
        int counter = 0;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxSub = itemView.findViewById(R.id.checkBox);
            textViewSub = itemView.findViewById(R.id.subNameTV);
        }
    }

    public ArrayList<SubjectFetchModel> getSelectedSubject(){
            ArrayList<SubjectFetchModel> subjectFetchModels = new ArrayList<>();
            for (int i = 0; i<subjectModelArrayList.size();i++){
                    if (subjectModelArrayList.get(i).isSelected){
                        subjectFetchModels.add(subjectModelArrayList.get(i));
                    }
            }
            return subjectFetchModels;
    }
}

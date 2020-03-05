package com.shawn.researjour.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shawn.researjour.Models.CategoryModel;
import com.shawn.researjour.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    public static ArrayList<CategoryModel> arrayListCategory;

    private int checkedPosition = 0;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> arrayListCategory){
            inflater = LayoutInflater.from(context);
            this.arrayListCategory = arrayListCategory;
            this.context = context;

    }

    public void setCategoryModel(ArrayList<CategoryModel> arrayListCategory){
            this.arrayListCategory = new ArrayList<>();
            this.arrayListCategory = arrayListCategory;
            notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.catview_item,parent,false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            holder.bind(arrayListCategory.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListCategory.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{

    private ImageView imageViewCategoryNoneSelected;
    private ImageView imageViewCatSelected;
    private TextView textViewCategory;
//    private ImageView imageViewtick;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategoryNoneSelected = itemView.findViewById(R.id.categoryImageNonSelected);
            imageViewCatSelected = itemView.findViewById(R.id.categoryImageSelected);
            textViewCategory = itemView.findViewById(R.id.categoryText);
//            imageViewtick = itemView.findViewById(R.id.selectionTick);
        }

        @SuppressLint("ResourceAsColor")
        void bind(final CategoryModel categoryModel){
            if (checkedPosition == -1){
//                imageViewtick.setVisibility(View.GONE);
                textViewCategory.setTextColor(Color.parseColor("#333333"));
                imageViewCatSelected.setVisibility(View.GONE);


            }else
            {
                if (checkedPosition==getAdapterPosition()){
//                    imageViewtick.setVisibility(View.GONE);
                    imageViewCatSelected.setVisibility(View.GONE);
                    textViewCategory.setTextColor(Color.parseColor("#333333"));

                }
                else {
//                    imageViewtick.setVisibility(View.GONE);
                    imageViewCatSelected.setVisibility(View.GONE);
                    textViewCategory.setTextColor(Color.parseColor("#333333"));

                }
            }
            textViewCategory.setText(categoryModel.textPosition);
            imageViewCategoryNoneSelected.setImageResource(categoryModel.imagePositionNoSelection);
            imageViewCatSelected.setImageResource(categoryModel.imagePositionSelection);

            itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
//                   imageViewtick.setVisibility(View.VISIBLE);
                    imageViewCatSelected.setVisibility(View.VISIBLE);

                    textViewCategory.setTextColor(Color.parseColor("#E83350"));

                    if (checkedPosition!=getAdapterPosition()){
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();

                    }

                }
            });
        }
    }
    public CategoryModel getSelected(){
        if (checkedPosition!= -1){
                return arrayListCategory.get(checkedPosition);
        }
        return null;
    }
}

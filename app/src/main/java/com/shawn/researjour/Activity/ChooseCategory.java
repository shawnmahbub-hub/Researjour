package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shawn.researjour.Adapter.CategoryAdapter;
import com.shawn.researjour.Models.CategoryModel;
import com.shawn.researjour.R;

import java.util.ArrayList;

public class ChooseCategory extends AppCompatActivity {

    private RecyclerView recyclerViewCategory;
    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private Button buttonChooseSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        recyclerViewCategory = findViewById(R.id.recycleViewCategory);

        buttonChooseSubject = findViewById(R.id.cat_next_button_id);

        categoryModels.add(new CategoryModel("Medical & Health Sciences", R.drawable.medi, R.drawable.medical));
        categoryModels.add(new CategoryModel("Life Sciences & Biology", R.drawable.bio, R.drawable.life));
        categoryModels.add(new CategoryModel("Engineering & Computer Science", R.drawable.cmt, R.drawable.engi));
        categoryModels.add(new CategoryModel("Chemistry & Materials Science", R.drawable.lab, R.drawable.chem));
        categoryModels.add(new CategoryModel("Social Sciences & Psychology", R.drawable.phsyco, R.drawable.social));
        categoryModels.add(new CategoryModel("Earth Sciences & Geography", R.drawable.geo, R.drawable.earth));
        categoryModels.add(new CategoryModel("Physics & Mathematics", R.drawable.math, R.drawable.phy));
        categoryModels.add(new CategoryModel("Business Management", R.drawable.manage, R.drawable.busi));
        categoryModels.add(new CategoryModel("Medical & Health Sciences", R.drawable.medi, R.drawable.medical));
        categoryModels.add(new CategoryModel("Life Sciences & Biology", R.drawable.bio, R.drawable.life));
        categoryModels.add(new CategoryModel("Engineering & Computer Science", R.drawable.cmt, R.drawable.engi));
        categoryModels.add(new CategoryModel("Chemistry & Materials Science", R.drawable.lab, R.drawable.chem));
        categoryModels.add(new CategoryModel("Social Sciences & Psychology", R.drawable.phsyco, R.drawable.social));
        categoryModels.add(new CategoryModel("Earth Sciences & Geography", R.drawable.geo, R.drawable.earth));
        categoryModels.add(new CategoryModel("Physics & Mathematics", R.drawable.math, R.drawable.phy));
        categoryModels.add(new CategoryModel("Business Management", R.drawable.manage, R.drawable.busi));



        categoryAdapter = new CategoryAdapter(this, categoryModels);
        categoryAdapter.setCategoryModel(categoryModels);
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(this,2));
        recyclerViewCategory.setAdapter(categoryAdapter);

        buttonChooseSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryAdapter.getSelected() !=null){
                    String passText = categoryAdapter.getSelected().textPosition;
                    Intent intent = new Intent(ChooseCategory.this,ChooseSubject.class);
                    intent.putExtra("msg",passText);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"no selection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

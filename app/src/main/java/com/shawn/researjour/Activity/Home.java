package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shawn.researjour.Fragments.BookmarksFragment;
import com.shawn.researjour.Fragments.HomeFragment;
import com.shawn.researjour.Fragments.MyResearchFragment;
import com.shawn.researjour.Fragments.ProfileFragment;
import com.shawn.researjour.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout homeFrameLayout;

    private androidx.appcompat.widget.Toolbar mTopToolbar;
    private HomeFragment homeFragment;
    private MyResearchFragment myResearchFragment;
    private BookmarksFragment bookmarksFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkUserStatus();

        mTopToolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        homeFrameLayout=(FrameLayout)findViewById(R.id.homeFragment_id);
        BottomNavigationView navigationView=findViewById(R.id.bottom_navigation);

        homeFragment=new HomeFragment();
        myResearchFragment=new MyResearchFragment();
        bookmarksFragment=new BookmarksFragment();
        profileFragment=new ProfileFragment();

        //default fragment
        setFragment(homeFragment);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //handle item clicks
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        //myResearch fragment transaction
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_myResearch:
                        //myResearch fragment transaction
                        setFragment(myResearchFragment);
                        return true;

                    case R.id.nav_bookmarks:
                        //myResearch fragment transaction
                        setFragment(bookmarksFragment);
                        return true;

                    case R.id.nav_profile:
                        //myResearch fragment transaction
                        setFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            //user is signed in
            String myUid = user.getUid();
        }else {
            //user not signed in, go to login activity
            startActivity(new Intent(this,Login.class));
            finish();}
    }


    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction2=getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
        fragmentTransaction2.replace(R.id.homeFragment_id, fragment, "");
        fragmentTransaction2.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.action_addPost){
            startActivity(new Intent(this, AddNewPost.class));
        }/*if (id==R.id.action_notification){
            Toast.makeText(this, "notification", Toast.LENGTH_SHORT).show();}*/
        return super.onOptionsItemSelected(item);
    }
}

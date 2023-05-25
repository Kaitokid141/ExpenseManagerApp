package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.expensemanager.Model.MyFirebaseMessagingService;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.common.math.Stats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.expensemanager.R.string.navigation_drawer_open;
import static com.example.expensemanager.R.string.pin_code_forgot_text;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    //Fragment
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;
    private StatsFragment statsFragment;
    private BudgetFragment budgetFragment;

    //Firebase
    private FirebaseAuth mAuth;

    public SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTimePin", true);
        Intent intent = new Intent(HomeActivity.this, CustomPinActivity.class);
        if(!isFirstTime){
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            startActivity(intent);
        }else{
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivity(intent);
        }
//        else {
//            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
//        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (
                        this,
                        drawerLayout,
                        toolbar,
                        navigation_drawer_open,
                        R.string.navigation_drawer_close
                )
        {};
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavbar);
        frameLayout = findViewById(R.id.main_frame);

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        statsFragment = new StatsFragment();
        budgetFragment = new BudgetFragment();

        setFragment(dashboardFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.dashboard:
                        setFragment(dashboardFragment);
                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);
                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);
                        return true;
                    case R.id.stats:
                        setFragment(statsFragment);
                        return true;
                    case R.id.budget:
                        setFragment(budgetFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
                Toast.makeText(this, "PinCode enabled", Toast.LENGTH_SHORT).show();
        }
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else{
            super.onBackPressed();
        }
    }
    public void displaySelectedListener(int itemId){
        Fragment fragment = null;

        switch(itemId){
            case android.R.id.home:
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
                fragment = new StatsFragment();
                return;
            case R.id.dashboard:
                bottomNavigationView.setSelectedItemId(R.id.dashboard);
                fragment = new DashboardFragment();
                break;

            case R.id.income:
                bottomNavigationView.setSelectedItemId(R.id.income);
                fragment = new IncomeFragment();
                break;

            case R.id.expense:
                bottomNavigationView.setSelectedItemId(R.id.expense);
                fragment = new ExpenseFragment();
                break;
            case R.id.stats:
                bottomNavigationView.setSelectedItemId(R.id.stats);
                bottomNavigationView.findViewById(R.id.stats).performClick();
                bottomNavigationView.performClick();
                fragment = new StatsFragment();
                break;
            case R.id.budget:
                bottomNavigationView.setSelectedItemId(R.id.budget);
                fragment = new BudgetFragment();
                break;
            case R.id.account:
                fragment = new AccountFragment();
                break;
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i("ITEM ID", Integer.toString(item.getItemId()));
        displaySelectedListener(item.getItemId());
        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Save boolean value to SharedPreferences
        preferences.edit().putBoolean("isFirstTimePin", false).apply();
    }

}
package com.example.dsi_android_ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static ViewGroup mainView = null;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        bottomNavigation.setSelectedItemId(R.id.search);
        mainView = findViewById(R.id.main_activity);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.tasks:
                            openFragment(TaskFragment.newInstance());
                            return true;
                        case R.id.work_cart:
                            openFragment(new CartFragment());
                            return true;
                        case R.id.search:
                            openFragment(SearchFragment.newInstance("",""));
                            return true;
                        case R.id.notification:
                            openFragment(NotificationFragment.newInstance("",""));
                            return true;
                        case R.id.setting:
                            openFragment(SettingFragment.newInstance("",""));
                            return true;
                    }
                    return false;
                }
            };

    public void changeBottomNavigation(int menuItemId){
        bottomNavigation.getMenu().findItem(menuItemId).setChecked(true);
    }

    public static ViewGroup getMainView() {
        return mainView;
    }
}
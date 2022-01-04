package com.minewbeacon.blescan.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yuliwuli.blescan.demo.R;

public class attendance extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();
    LinearLayout home_ly;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        init(); //객체 정의
        SettingListener();
        // 리스너 등록
        // 맨 처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);
    }

    private void init() {
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, new HomeFragment()).commit();
                    return true;
                }
                case R.id.tab_home1: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, new Home1()).commit();
                    return true;
                }
                case R.id.tab_home2: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, new Home2()).commit();
                    return true;
                }
            }
            return false;
        }
    }


}
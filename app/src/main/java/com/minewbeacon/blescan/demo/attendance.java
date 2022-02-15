package com.minewbeacon.blescan.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.minewbeacon.blescan.demo.activity.Home1;
import com.minewbeacon.blescan.demo.activity.Home2;
import com.minewbeacon.blescan.demo.activity.HomeFragment;
import com.yuliwuli.blescan.demo.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class attendance extends AppCompatActivity {

    private BeaconManager beaconManager;
    private static final int PERMISSION_REQUESTS = 1;
    Region myRegion;

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

        //check for permissions and start the beacons.
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //added eddystone, since I'm moving from google's beacons to altbeacon.  RedBeacon can broadcast both.
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        startexample();
    }

    private void init() {
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnItemSelectedListener(new TabSelectedListener());
    }

    void startexample() {

        // Set up a Live Data observer so this Activity can get monitoring callbacks
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        myRegion = new Region("myMonitoringUniqueId", null, null, null);
        //first the monitor

        beaconManager.getRegionViewModel(myRegion).getRegionState().observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer state) {
                        if (state == MonitorNotifier.INSIDE) {
                            Toast.makeText(getApplicationContext(), "Detected beacons(s)", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Stopped detecting beacons", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        beaconManager.startMonitoring(myRegion);

        beaconManager.getRegionViewModel(myRegion).getRangedBeacons().observe(this, new Observer<Collection<Beacon>>() {
            @Override
            public void onChanged(Collection<Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    String DeviceId = String.valueOf(beacon.getId1());
                    String DeviceName = beacon.getBluetoothName();
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {

                    } else if (DeviceId.equals("e2c56db5-dffb-48d2-b060-d0f5a71096e0")  && DeviceName.equals("bluzent")) {
                        //Bluzent Beacon
                        Toast.makeText(getApplicationContext(), "거리:" + beacon.getDistance(), Toast.LENGTH_SHORT).show();
                        if(beacon.getDistance()<5){
                            Toast.makeText(getApplicationContext(), "5M이하 접근:", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        //no clue what we found here.

                        Toast.makeText(getApplicationContext(), "거리:" + beacon.getId1(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        beaconManager.startRangingBeacons(myRegion);
    }


    @Override
    protected void onStop() {
        beaconManager.stopMonitoring( myRegion );
        beaconManager.stopRangingBeacons( myRegion );
        super.onStop();
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
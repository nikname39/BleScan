package com.bluzent.mybluzent.demo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bluzent.mybluzent.demo.GpsTracker;
import com.bluzent.mybluzent.demo.R;
import com.bluzent.mybluzent.demo.Utils;
import com.bluzent.mybluzent.demo.activity.attendanceFragment.Home1;
import com.bluzent.mybluzent.demo.activity.attendanceFragment.Home2;
import com.bluzent.mybluzent.demo.activity.attendanceFragment.HomeFragment;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collection;

public class attendance extends AppCompatActivity {

    private BeaconManager beaconManager;
    private static final int PERMISSION_REQUESTS = 1;
    Region myRegion;

    MqttAndroidClient client;

    static String MQTTHOST = "tcp://3.38.101.34:1883";;
    static String USERNAME = "ebluzent_sub1";
    static String PASSWORD = "1234";

    public Boolean DistanceCheck = false;



    final Handler handler = new Handler();

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

        GpsTracker gpsTracker = new GpsTracker(attendance.this);
        double currentLatitude = gpsTracker.getLatitude();
        double currentLongitude = gpsTracker.getLongitude();

        Toast.makeText(getApplicationContext(), "Lat"+currentLatitude+"Long"+currentLongitude, Toast.LENGTH_SHORT).show();

        //MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe("SongDo/DoorLock1", 0 );   //연결에 성공하면 SongDo/DoorLock1 라는 토픽으로 subscribe함
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        //check for permissions and start the beacons.
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setForegroundBetweenScanPeriod(3000);
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
                            //Toast.makeText(getApplicationContext(), "Detected beacons(s)", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Toast.makeText(getApplicationContext(), "Stopped detecting beacons", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );



        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                DistanceCheck=false;
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });
        beaconManager.startMonitoring(myRegion);
//쓰레드
//        beaconManager.getRegionViewModel(myRegion).getRangedBeacons().observe(this, new Observer<Collection<Beacon>>() {
//            @Override
//            public void onChanged(Collection<Beacon> beacons) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (Beacon beacon : beacons) {
//                            String DeviceId = String.valueOf(beacon.getId1());
//                            String DeviceName = beacon.getBluetoothName();
//                            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
//
//                            } else if (DeviceId.equals("e2c56db5-dffb-48d2-b060-d0f5a71096e0")  && DeviceName.equals("bluzent")) {
//                                Log.i(TAG, "거리: "+beacon.getDistance());
//                                //Bluzent Beacon
//                                //Toast.makeText(getApplicationContext(), "거리:" + beacon.getDistance(), Toast.LENGTH_SHORT).show();
//                                if(beacon.getDistance()<2){
//                                    if(DistanceCheck==false) {
//                                        DistanceCheck=true;
//                                        Toast.makeText(getApplicationContext(), "5M이하 접근:", Toast.LENGTH_SHORT).show();
//                                        String topic = "SongDo/DoorLock1";
//                                        String message = "1";
//                                        try {
//                                            client.publish(topic, message.getBytes(), 2, false);
//                                        } catch (MqttException e) {
//                                            e.printStackTrace();
//                                        }
//                                        //Toast.makeText(getApplicationContext(), "5초 후 자동으로 닫힙니다.", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    DistanceCheck=false;
//                                }
//                            } else {
//                                //no clue what we found here.
//                                Toast.makeText(getApplicationContext(), "거리:" + beacon.getId1(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//
//                    }
//                });
//
//            }
//        });

        beaconManager.getRegionViewModel(myRegion).getRangedBeacons().observe(this, new Observer<Collection<Beacon>>() {
            @Override
            public void onChanged(Collection<Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    String DeviceId = String.valueOf(beacon.getId1());
                    String DeviceName = beacon.getBluetoothName();
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {

                    } else if (DeviceId.equals("e2c56db5-dffb-48d2-b060-d0f5a71096e0")  && DeviceName.equals("bluzent")) {
                        Log.i(TAG, "------------------------------------------------거리: "+beacon.getDistance());
                        Log.i(TAG, "------------------------------------------------거리: "+ Utils.calculateAccuracy(beacon.getTxPower(),beacon.getRssi()));
                        Log.i(TAG, "------------------------------------------------tx: "+beacon.getTxPower());
                        Log.i(TAG, "------------------------------------------------------------------------------------------------: "+beacon.getTxPower());
                        //Toast.makeText(getApplicationContext(), "거리: "+Utils.calculateAccuracy(beacon.getTxPower(),beacon.getRssi()), Toast.LENGTH_SHORT).show();


                        //Bluzent Beacon
                        //Toast.makeText(getApplicationContext(), "거리:" + beacon.getDistance(), Toast.LENGTH_SHORT).show();
                        if(Utils.calculateAccuracy(beacon.getTxPower(),beacon.getRssi())<2){
                            if(DistanceCheck==false) {
                                DistanceCheck=true;
                                Toast.makeText(getApplicationContext(), "5M이하 접근:", Toast.LENGTH_SHORT).show();
                                String topic = "SongDo/DoorLock1";
                                String message = "1";
                                try {
                                    client.publish(topic, message.getBytes(), 2, false);
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(getApplicationContext(), "5초 후 자동으로 닫힙니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            DistanceCheck=false;
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







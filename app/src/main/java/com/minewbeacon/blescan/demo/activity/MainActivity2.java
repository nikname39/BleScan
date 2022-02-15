/**
 * login controller
 * by jh
 */
package com.minewbeacon.blescan.demo.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import com.minewbeacon.blescan.demo.PermissionSupport;
import com.minewbeacon.blescan.demo.Post;
import com.minewbeacon.blescan.demo.Register;
import com.minewbeacon.blescan.demo.RetrofitAPI;
import com.minewbeacon.blescan.demo.UserAccount;
import com.minewbeacon.blescan.demo.UserRssi;
import com.minewbeacon.blescan.demo.Utils;
import com.minewbeacon.blescan.demo.attendance;
import com.yuliwuli.blescan.demo.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    ProgressDialog dialog;

    Intent serviceIntent;

    // 클래스 선언
    private PermissionSupport permission;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;
    private boolean isScanning;
    private static final int REQUEST_ENABLE_BT = 2;
    public static Context mContext;
    public static Boolean check;
    private final String DEFAULT = "DEFAULT";

    UserRssi comp = new UserRssi();

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd;
    private TextView mUserName;
    private CheckBox mCheckbox;
    private Button mLogin, mRegister;
    private int state;

    private MinewBeaconManager mMinewBeaconManager;

    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }
        });

        beaconManager.startRangingBeacons(new Region("myRangingUniqueId", null, null, null));


        //retrofit 사용
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.getData("1").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()) {
                    List<Post> data = response.body();
//                    for (Post post : data) {
//                        String content = "";
//                        content += "ID: " + post.getId() + "\n";
//                        content += "Title: " + post.getTitle() + "\n";
//                        content += "Content: " + post.getBody() + "\n\n";
//                        textViewResult.append(content);
//                    }
                    Toast.makeText(MainActivity2.this, data.get(1).getTitle(), Toast.LENGTH_SHORT).show();
                    Log.d("Test", "성공");
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "실패.", Toast.LENGTH_SHORT).show();
            }
        });

        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        //progressBar.setProgress(80);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bluzent");

        //mEtEmail = findViewById(R.id.Lg_email);
        //mEtPwd = findViewById(R.id.Lg_pwd);
        mCheckbox = findViewById(R.id.autoLogin);
        mContext = this;
        mUserName = findViewById(R.id.userName);

        Utils.setBoolean(mContext, "Overwork", false);

        //블루투스, 위치권한 체크
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);

        checkBluetooth();
        checkLocationPermition();
        checkSSAID();
        checkAutologin();

    }

//    // 권한 체크
//    private void permissionCheck() {
//
//        // PermissionSupport.java 클래스 객체 생성
//        permission = new PermissionSupport(this, this);
//
//        // 권한 체크 후 리턴이 false로 들어오면
//        if (!permission.checkPermission()){
//            //권한 요청
//            Toast.makeText(this, "권한false.", Toast.LENGTH_SHORT).show();
//            permission.requestPermission();
//        }
//    }

//    // Request Permission에 대한 결과 값 받아와
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
//        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
//            // 다시 permission 요청
//            permission.requestPermission();
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


//    /**
//     * 알림서비스 실행
//     */
//    public void startService()
//    {
//        serviceIntent = new Intent(this, MyService.class);
//        startService(serviceIntent);
//    }
//
//    /**
//     * 알림서비스 중지
//     */
//    public void stopService()
//    {
//        serviceIntent = new Intent(this, MyService.class);
//        stopService(serviceIntent);
//    }

    //위치 권한 체크
    private void checkLocationPermition() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 위치정보 설정 Intent
            new AlertDialog.Builder(MainActivity2.this)
                    .setTitle("경고")
                    .setMessage("GPS가 꺼져있습니다.\n ‘위치 서비스’에서 ‘Google 위치 서비스’를 체크해주세요")
                    .setCancelable(false)	// Back Button 동작 안하도록 설정

                    // "계속" 버튼
                    .setPositiveButton("설정",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                        }
                    })

                    // "종료" 버튼
                    .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity2.this, "종료 버튼을 누르셨습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .show();
        }
    }

    // 블루투스 권한 체크
    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "BLE를 지원하지않습니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                Toast.makeText(this, "블루투스 기능을 켜주세요.", Toast.LENGTH_SHORT).show();
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }

    //블루투스 권한요청
    private void showBLEDialog() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    // SSAID 정보 가져오기
    /**
     * check SSAID state
     */
    private void checkSSAID() {

        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bluzent");

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        UserAccount account = new UserAccount();

        //account.setIdToken(firebaseUser.getUid());
        //account.setEmailId(firebaseUser.getEmail());
        account.setAndroid_Id(androidId);

        mDatabaseRef.child("UserAccount").child(androidId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                String value1 = datasnapshot1.getValue(String.class);

                if (value1 == null) {
                    Toast.makeText(MainActivity2.this, "가입 정보가 없습니다. 회원가입 바랍니다.", Toast.LENGTH_SHORT).show();
                    Utils.setBoolean(mContext, "checked", false);

                } else {
                    Toast.makeText(MainActivity2.this, "SSAID 불러오기 성공", Toast.LENGTH_SHORT).show();
                    mUserName.setText(value1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //자동로그인 설정
    private  void checkAutologin() {


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        }
        else {
            BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
            switch (bluetoothState) {
                case BluetoothStateNotSupported:
                    Toast.makeText(this, "BLE를 지원하지않습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case BluetoothStatePowerOff:
                    Toast.makeText(this, "블루투스 기능을 켜주세요.", Toast.LENGTH_SHORT).show();
                    showBLEDialog();
                    break;
                case BluetoothStatePowerOn:
                    //자동로그인 확인
                    Boolean check = Utils.getBoolean(mContext, "checked");
                    if (check == null) {
                        Utils.setBoolean(mContext, "checked", false);
                    } else {
                        if (String.valueOf(check).equals("true")) {
                            mCheckbox.setChecked(true);
                            Utils.setBoolean(mContext, "checked", true);
                            Button mLogin = (Button) findViewById(R.id.btn_login);
                            mLogin.callOnClick();

                        } else {
                            mCheckbox.setChecked(false);
                            Utils.setBoolean(mContext, "checked", false);
                        }
                    }
                    break;
            }
        }

    }

    //로그인 버튼
    public void myListener(View target) {
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        dialog = new ProgressDialog(MainActivity2.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("비콘 신호를 확인하는 중입니다.");
        dialog.show();

//        Toast.makeText(getApplicationContext(), "스캔1.", Toast.LENGTH_SHORT).show();
//        Button mLogin = (Button) findViewById(R.id.btn_login);
//        mLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
        String BeaconName;
        Log.e("Click1", "");
        if (mMinewBeaconManager != null) {
            BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
            Log.e("Click2", "");
            switch (bluetoothState) {
                case BluetoothStateNotSupported:
                    Toast.makeText(MainActivity2.this, "BLE를 지원하지않습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case BluetoothStatePowerOff:
                    showBLEDialog();
                    return;
                case BluetoothStatePowerOn:
                    break;
            }
        }
        Log.e("Click3", "");
        //Toast.makeText(getApplicationContext(), "비컨 신호를 인식하지 못했습니다.", Toast.LENGTH_SHORT).show();

        try {
            mMinewBeaconManager.startScan();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //블루투스 감지 이벤트
        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {

            /**
             *   if the manager find some new beacon, it will call back this method.
             *
             *  @param minewBeacons  new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                Toast.makeText(getApplicationContext(),   "새로운 비콘 신호가 탐지되었습니다.", Toast.LENGTH_SHORT).show();
            }

            /**
             *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             *  @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                    Toast.makeText(getApplicationContext(),   "비콘 신호가 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                    onDestroy();
            }

            /**
             *  the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             *  @param minewBeacons all scanned beacons
             */
            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(minewBeacons, comp);
                        Log.e("스테이트 상태", state + "");

                        for (MinewBeacon minewBeacon : minewBeacons) {

                            String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();

                            //int deviceRssi = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getIntValue();
                            //Toast.makeText(getApplicationContext(), "거리:" + deviceRssi, Toast.LENGTH_SHORT).show();

                            String Name = "bluzent";


                            if(deviceName.equals(Name)){
                                //Toast.makeText(getApplicationContext(), "로그인 정보 체크중.", Toast.LENGTH_SHORT).show();
                                //로그인 정보 체크
                                dialog.setMessage("로그인 정보를 확인하는 중입니다.");

                                try {
                                    mFirebaseAuth = FirebaseAuth.getInstance();
                                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("bluzent");

                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                    UserAccount account = new UserAccount();  //VO
                                    //account.setIdToken(firebaseUser.getUid());
                                    //account.setEmailId(firebaseUser.getEmail());
                                    account.setAndroid_Id(androidId);

                                    mDatabaseRef.child("UserAccount").child(androidId).child("android_Id").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                                            String value1 = datasnapshot1.getValue(String.class);

                                            if (value1 == null){
                                                Utils.setBoolean(mContext, "checked", false);
                                                Toast.makeText(getApplicationContext(), "회원가입 바랍니다.", Toast.LENGTH_SHORT).show();
                                                mMinewBeaconManager.stopScan();
                                                ActivityCompat.finishAffinity(MainActivity2.this);
                                            } else {
                                                if (value1.equals(androidId)) {
                                                    // 로그인 성공
                                                    Toast.makeText(MainActivity2.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();

                                                    CheckBox Autologin = findViewById(R.id.autoLogin);

                                                    mMinewBeaconManager.stopScan();
                                                    Intent intent = new Intent(getApplicationContext(), attendance.class);

                                                    startActivity(intent);
                                                    finish();

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                                    mMinewBeaconManager.stopScan();
                                                }
                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                    mMinewBeaconManager.stopScan();
                                }
//                                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                                Toast.makeText(MainActivity.this, "블루젠트 비콘 신호 감지", Toast.LENGTH_SHORT).show();
//                                startActivity(intent);
//                                finish();
                            } else{
                                //Toast.makeText(MainActivity.this, "신호 없음", Toast.LENGTH_SHORT).show();
                                //Wn.setText("비콘 ID: "+deviceName+"설정 ID: "+Name+"");
                                Toast.makeText(getApplicationContext(), "비컨 신호를 인식하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(MainActivity2.this, "블루젠트 비콘 신호 미감지", Toast.LENGTH_SHORT).show();
                                mMinewBeaconManager.stopScan();
                            }
                        }
                        if (state == 1 || state == 2) {
                        } else {

                        }

//                        try {
//                            Thread.sleep(5000);
//                        } catch (Exception e) {
//                            e.printStackTrace() ;
//                        }
                    }
                });

            }

            /**
             *  the manager calls back this method when BluetoothStateChanged.
             *
             *  @param state BluetoothState
             */
            @Override
            public void onUpdateState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    //자동로그인 버튼
    public void myListener2(View target) {

        CheckBox Autologin = findViewById(R.id.autoLogin);
        Boolean checked = ((CheckBox) target).isChecked();

        if (checked) {
            Toast.makeText(getApplicationContext(), "자동로그인이 설정되었습니다.", Toast.LENGTH_SHORT).show();
            Utils.setBoolean(mContext, "checked", true);

        } else {
            Toast.makeText(getApplicationContext(), "자동로그인이 해제되었습니다.", Toast.LENGTH_SHORT).show();
            Utils.setBoolean(mContext, "checked", false);
        }
    }

    //회원가입 버튼
    public void myListener3(View target) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop scan
        if (isScanning) {
            mMinewBeaconManager.stopScan();
        }
    }

    public static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }




}


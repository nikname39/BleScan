/**
 * login controller
 * by jh
 */
package com.minewbeacon.blescan.demo;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
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
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import com.yuliwuli.blescan.demo.R;

import java.util.Collections;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ProgressDialog dialog;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;
    private boolean isScanning;
    private static final int REQUEST_ENABLE_BT = 2;

    UserRssi comp = new UserRssi();

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd;
    private Context mContext;
    private CheckBox mCheckbox;
    private Button mLogin, mRegister;
    private int state;


    private MinewBeaconManager mMinewBeaconManager;
    MainActivity MA = (MainActivity) MainActivity.activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //MA.finish();
        //Toast.makeText(MainActivity2.this, "블루젠트 이동", Toast.LENGTH_SHORT).show();

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        //progressBar.setProgress(80);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bluzent");

        mEtEmail = findViewById(R.id.Lg_email);
        mEtPwd = findViewById(R.id.Lg_pwd);
        mCheckbox = findViewById(R.id.autoLogin);
        mContext = this;

        // 자동로그인 확인
        boolean check = PreferenceManager.getBoolean(mContext, "checked");

        if (String.valueOf(check).equals("true")) {
            mCheckbox.setChecked(true);
            mEtEmail.setText(PreferenceManager.getString(mContext, "ID"));
            mEtPwd.setText(PreferenceManager.getString(mContext, "PW"));
        } else {
            mCheckbox.setChecked(false);
            mEtEmail.setText("");
            mEtPwd.setText("");
        }

        //블루투스, 위치권한 체크
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
        checkBluetooth();
        checkLocationPermition();

    }

    //위치 권한 체크
    private void checkLocationPermition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                // 권한 없음
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);
            } else{
                // ACCESS_FINE_LOCATION 에 대한 권한이 이미 있음.
            }
        }
// OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else{
        }
    }
    // 블루투스 권한 체크
    /**
     * check Bluetooth state
     */
    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "BLE를 지원하지않습니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }

    //
    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    }



    //로그인 버튼
    public void myListener(View target) {

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
//            }
//        });

        //블루투스 감지 이벤트
        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            /**
             *   if the manager find some new beacon, it will call back this method.
             *
             *  @param minewBeacons  new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {

            }

            /**
             *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             *  @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                /*for (MinewBeacon minewBeacon : minewBeacons) {
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }*/
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
                            dialog = new ProgressDialog(MainActivity2.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("로그인 정보를 확인하는 중입니다.");
                            dialog.show();

                            String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                            //String deviceName = "luzent";
                            //Toast.makeText(getApplicationContext(), deviceName + " 발견", Toast.LENGTH_SHORT).show();
                            String Name = "bluzent";
                            //System.out.print(deviceName);

                            EditText Wn = (EditText) findViewById(R.id.editTextTextPersonName2);
                            //mMinewBeaconManager.stopScan();
                            Log.e("tag", String.valueOf(isScanning));

                            if(deviceName.equals(Name)){
                                //Toast.makeText(getApplicationContext(), "로그인 정보 체크중.", Toast.LENGTH_SHORT).show();
                                //로그인 정보 체크
                                String strEmail = mEtEmail.getText().toString();
                                String strPwd = mEtPwd.getText().toString();
                                try {
                                    mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(MainActivity2.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // 로그인 성공
                                                Toast.makeText(MainActivity2.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();

                                                String strEmail = mEtEmail.getText().toString();
                                                String strPwd = mEtPwd.getText().toString();

                                                CheckBox Autologin = findViewById(R.id.autoLogin);
                                                boolean checked = Autologin.isChecked();
                                                if (checked) {
                                                    PreferenceManager.setString(mContext, "ID", strEmail);
                                                    PreferenceManager.setString(mContext, "PW", strPwd);
                                                    PreferenceManager.setBoolean(mContext, "checked", true);
                                                } else {
                                                    PreferenceManager.setString(mContext, "ID", "");
                                                    PreferenceManager.setString(mContext, "PW", "");
                                                    PreferenceManager.setBoolean(mContext, "checked", false);
                                                }
                                                mMinewBeaconManager.stopScan();
                                                Intent intent = new Intent(getApplicationContext(), attendance.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity2.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                                                mMinewBeaconManager.stopScan();
                                            }
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

    //회원가입 버튼
    public void myListener2(View target) {

        String strEmail = mEtEmail.getText().toString();
        String strPwd = mEtPwd.getText().toString();

        CheckBox Autologin = findViewById(R.id.autoLogin);
        Autologin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();

                if (checked) {
                    Toast.makeText(getApplicationContext(), String.valueOf(checked), Toast.LENGTH_SHORT).show();
                    PreferenceManager.setString(mContext, "ID", strEmail);
                    PreferenceManager.setString(mContext, "PW", strPwd);
                    PreferenceManager.setBoolean(mContext, "checked", true);

                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(checked), Toast.LENGTH_SHORT).show();
                    PreferenceManager.setString(mContext, "ID", "");
                    PreferenceManager.setString(mContext, "PW", "");
                    PreferenceManager.setBoolean(mContext, "checked", false);
                }
            }

        });

    }

    public void myListener3(View target) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop scan
        if (isScanning) {
            mMinewBeaconManager.stopScan();
        }
    }



}


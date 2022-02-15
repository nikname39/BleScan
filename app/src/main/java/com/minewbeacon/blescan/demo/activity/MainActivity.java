/**
 * bluetooth susin 현재 안씀
 * by jh
 */

package com.minewbeacon.blescan.demo.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minewbeacon.blescan.demo.BeaconListAdapter;
import com.minewbeacon.blescan.demo.UserRssi;
import com.yuliwuli.blescan.demo.R;

public class MainActivity extends AppCompatActivity {



    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;
    private MinewBeaconManager mMinewBeaconManager;
    private MinewBeacon mMinewBeacon;
    private RecyclerView mRecycle;
    private BeaconListAdapter mAdapter;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning;

    UserRssi comp = new UserRssi();
    private TextView mStart_scan;
    private boolean mIsRefreshing;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLoading();
        //checkBluetooth();
        checkLocationPermition();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);  //Loagin화면을 띄운다.
                finish();   //현재 액티비티 종료
            }
        }, 2000); // 화면에 Logo 2초간 보이기
    }// startLoading Method..

    //위치권한체크
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

    //블루투스 체크
    private void checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                break;
        }
    }
}

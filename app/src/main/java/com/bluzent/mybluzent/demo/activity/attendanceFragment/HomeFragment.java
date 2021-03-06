/**
 * work_start and end
 * by jh
 */
package com.bluzent.mybluzent.demo.activity.attendanceFragment;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.bluzent.mybluzent.demo.GpsTracker;
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
import com.bluzent.mybluzent.demo.MyService;
import com.bluzent.mybluzent.demo.R;
import com.bluzent.mybluzent.demo.UserAccount;
import com.bluzent.mybluzent.demo.Utils;
import com.bluzent.mybluzent.demo.activity.MainActivity2;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static int a;
    private final String DEFAULT = "DEFAULT";
    private MinewBeaconManager mMinewBeaconManager;
    public TextView mDistanceView;

    // TODO: Rename and change types of parameters
    private String mParam1 = "00???00???";
    private String mParam2 = "00???00???";
    private int mintParam1, mintParam2;
    private TextView mDisplayDate, mOnworkView, mOffworkView, mTodayworkView;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private Button mBtnOnWork;
    private Button mBtnOffWork;
    private String Name = "";
    private String Work = "";
    private Context mContext;
    private CheckBox mCheckbox;

    //Naver Api
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    final Handler handler = new Handler();

    public HomeFragment() {
        // Required empty public constructor
    }

    void createNotificationChannel(String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    void createNotification(String channelId, int id, String title, String text) {
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.bgs);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)          // Head Up Display??? ?????? PRIORITY_HIGH ??????
                .setSmallIcon(R.drawable.bgs)        // ????????? ???????????? ?????????. ????????? ??????
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(bitmap)
                //.setTimeoutAfter(1000)    // ????????? ?????? ?????? ?????? ??????
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))          // ?????? ????????? ???????????? ?????? ???????????? ????????? ??????
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);  // ????????? ?????????, ?????? ??????

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

        mContext = ((MainActivity2) MainActivity2.mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mDistanceView = (TextView) v.findViewById(R.id.DistanceView);

        mDistanceView.setText("???");

        NaverApiInit();





    //??????????????? ?????? ????????????
    CheckBox Autologin = v.findViewById(R.id.autoLogin);

    Boolean check = Utils.getBoolean(mContext, "checked");
    mCheckbox =v.findViewById(R.id.autoLogin);

        if(check ==null)

    {
        Utils.setBoolean(mContext, "checked", false);
    } else

    {
        if (String.valueOf(check).equals("true")) {
            mCheckbox.setChecked(true);
        } else {
            mCheckbox.setChecked(false);
        }
    }


    String androidId = Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
    //.. DAte
    mDisplayDate =(TextView)v.findViewById(R.id.tvDate);
    mOnworkView =(TextView)v.findViewById(R.id.OnworkView);
    mOffworkView =(TextView)v.findViewById(R.id.OffworkView);
    mTodayworkView =(TextView)v.findViewById(R.id.TodayworkView);

    //?????? ?????? ?????????
    Calendar cal = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //cal.add(Calendar.DATE, +1);

        cal.add(Calendar.DATE,-1);
    String YesterdayDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE,+1);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    int dayNum = cal.get(Calendar.DAY_OF_WEEK);

    String date = dateFormat.format(cal.getTime());
    String Timedate = hour + "???" + minute + "???";
    String currenttime = String.valueOf(hour * 60 + minute);

    //????????????
    String dayString = "";
        switch(dayNum)

    {
        case 1:
            dayString = "???";
            break;
        case 2:
            dayString = "???";
            break;
        case 3:
            dayString = "???";
            break;
        case 4:
            dayString = "???";
            break;
        case 5:
            dayString = "???";
            break;
        case 6:
            dayString = "???";
            break;
        case 7:
            dayString = "???";
            break;
    }

    //mDisplayDate.setText(date+" "+dayString+"??????");


        mDisplayDate.setText(date);


    //????????? ?????? ????????????


//        mDatabaseRef.child("Attendance").child(date).child(mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").toString()).child("work_start").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
//                String value1 = datasnapshot1.getValue(String.class);
//                Work = value1;
//                mOnworkView.setText("????????? ?????? ??????:"+Work);
//                Toast.makeText(getActivity(), Work, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
//            }
//        });

        try

    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {


                    //?????? ?????? ?????????
                    Calendar cal = Calendar.getInstance();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    //cal.add(Calendar.DATE, +1);

                    cal.add(Calendar.DATE, -1);
                    String YesterdayDate = dateFormat.format(cal.getTime());

                    cal.add(Calendar.DATE, +1);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);
                    int dayNum = cal.get(Calendar.DAY_OF_WEEK);

                    String date = dateFormat.format(cal.getTime());
                    String Timedate = hour + "???" + minute + "???";
                    String currenttime = String.valueOf(hour * 60 + minute);

                    //????????????
                    String dayString = "";
                    switch (dayNum) {
                        case 1:
                            dayString = "???";
                            break;
                        case 2:
                            dayString = "???";
                            break;
                        case 3:
                            dayString = "???";
                            break;
                        case 4:
                            dayString = "???";
                            break;
                        case 5:
                            dayString = "???";
                            break;
                        case 6:
                            dayString = "???";
                            break;
                        case 7:
                            dayString = "???";
                            break;
                    }

                    //mDisplayDate.setText(date+" "+dayString+"??????");
                    mDisplayDate.setText(date);
                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                    UserAccount account = new UserAccount();
                    //account.setIdToken(firebaseUser.getUid());
                    //account.setEmailId(firebaseUser.getEmail());
                    account.setWork_start(Timedate);
                    account.setWork_end(Timedate);
                    account.setTime_work_start(currenttime);
                    account.setTime_work_end(currenttime);


                    mDatabaseRef.child("UserAccount").child(androidId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            Name = value;

                            //?????? ?????? ????????????
                            mDatabaseRef.child("Attendance").child(date).child(Name).child("work_start").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                                    String value1 = datasnapshot1.getValue(String.class);
                                    mParam1 = value1;

                                    if (value1 == null) {
                                        //mOnworkView.setText("????????? ?????? ??????: ??????");
                                        mOnworkView.setText("????????? ?????? ??????:" + Timedate);
                                        //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_start").setValue(currenttime);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("work_start").setValue(Timedate);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("android_Id").setValue(androidId);
                                        mParam1 = Timedate;
                                    } else {
                                        mOnworkView.setText("????????? ?????? ??????: " + mParam1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //?????? ?????? ????????????
                            mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot2) {
                                    //mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").removeValue();
                                    String value2 = datasnapshot2.getValue(String.class);
                                    mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").removeEventListener(this);
                                    mParam2 = value2;
                                    if (value2 == null) {
                                        //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_end").setValue(currenttime);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").setValue(Timedate);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("android_Id").setValue(androidId);
                                        mOffworkView.setText("????????? ?????? ??????: ??????");
                                        mParam2 = Timedate;

                                    } else {
                                        //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_end").setValue(currenttime);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").setValue(Timedate);
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("android_Id").setValue(androidId);
                                        mOffworkView.setText("????????? ?????? ??????: " + Timedate);
                                    }

                                    //???????????? ?????????
                                    mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_start").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot datasnapshot3) {
                                            String value3 = datasnapshot3.getValue(String.class);
                                            String Todaywork_Hour = String.valueOf((Integer.parseInt(currenttime) - Integer.parseInt(value3)) / 60);
                                            String Todaywork_Minutes = String.valueOf((Integer.parseInt(currenttime) - Integer.parseInt(value3)) % 60);

                                            int mintParam1 = Integer.parseInt(mParam1.substring(0, mParam1.indexOf("???")));
                                            int mintParam2 = Integer.parseInt(mParam2.substring(0, mParam2.indexOf("???")));

                                            if (value3 == null) {
                                                mOnworkView.setText("????????? ?????? ??????: ??????");
                                                //mOnworkView.setText("????????? ?????? ??????:" + mParam1);
                                                //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                                mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_start").setValue(currenttime);
                                                mDatabaseRef.child("Attendance").child(date).child(Name).child("work_start").setValue(Timedate);
                                            } else {

                                                if ((mintParam1 <= 12) && (14 <= mintParam2)) {
                                                    Todaywork_Hour = String.valueOf(Integer.parseInt(Todaywork_Hour) - 1);
                                                    mOnworkView.setText("????????? ?????? ??????: " + mParam1);
                                                    mTodayworkView.setText("????????? ?????? ??????: " + Todaywork_Hour + "??????" + Todaywork_Minutes + "???");
                                                } else {
                                                    mOnworkView.setText("????????? ?????? ??????: " + mParam1);
                                                    mTodayworkView.setText("????????? ?????? ??????: " + Todaywork_Hour + "??????" + Todaywork_Minutes + "???");
                                                }
                                            }
                                            //???????????? 10?????? ????????? ????????????
                                            if (Integer.parseInt(Todaywork_Hour) >= 10) {


                                                Boolean overwork = Utils.getBoolean(mContext, "Overwork");

                                                if (overwork == null) {
                                                    Utils.setBoolean(mContext, "Overwork", false);
                                                } else {
                                                    if (String.valueOf(overwork).equals("true")) {

                                                    } else {
                                                        Utils.setBoolean(mContext, "Overwork", true);
                                                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                                        v.vibrate(new long[]{500, 1000, 500, 2000}, -1);
                                                        createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
                                                        createNotification(DEFAULT, 2, "??????", "??????????????? 10????????? ?????????????????????.");
                                                        Toast.makeText(getActivity(), "??????????????? 10????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                                                    }
                                                }


                                            }
                                            //?????? ?????? ??????
                                            if ((0 <= mintParam1) && (mintParam1 <= 3) && (0 <= mintParam2) && (mintParam2 <= 3)) {

                                                try {
                                                    mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot datasnapshot4) {
                                                            String value4 = datasnapshot4.getValue(String.class);
                                                            String nextday = hour + 24 + "???" + minute + "???";
                                                            String nextdaycurrenttime = String.valueOf(hour * 60 + minute + 1440);

                                                            mDatabaseRef.child("Attendance").child(YesterdayDate).child(Name).child("work_end").setValue(nextday);
                                                            mDatabaseRef.child("Attendance").child(YesterdayDate).child(Name).child("Time_work_end").setValue(nextdaycurrenttime);
                                                            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                                            v.vibrate(new long[]{500, 1000, 500, 2000}, -1);
                                                            mOnworkView.setText("????????? ?????? ??????: ?????? ??????");
                                                            mOffworkView.setText("????????? ?????? ??????: " + Timedate);
                                                            Toast.makeText(getActivity(), "?????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                                            mDatabaseRef.child("Attendance").child(date).child(Name).removeValue();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                } catch (Exception e) {
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
                        }
                    });
                    //????????? ??? ????????? ?????? ??????


                    mMinewBeaconManager = MinewBeaconManager.getInstance(mContext);
                    if (mMinewBeaconManager != null) {

                    }
                    try {
                        //mMinewBeaconManager.startScan();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //???????????? ?????? ?????????
                    mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
                        /**
                         *   if the manager find some new beacon, it will call back this method.
                         *
                         *  @param minewBeacons  new beacons the manager scanned
                         */
                        @Override
                        public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                            for (MinewBeacon minewBeacon : minewBeacons) {
                                String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                                String Name = "bluzent";
                                if (deviceName.equals(Name)) {
                                    //Toast.makeText(getActivity(), deviceName + "  ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                                    //break Loop1;
                                    //android.os.Process.killProcess(android.os.Process.myPid()); // ??? ???????????? ??????
                                }
                            }
                        }

                        /**
                         *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
                         *
                         *  @param minewBeacons beacons out of range
                         */
                        @Override
                        public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                            for (MinewBeacon minewBeacon : minewBeacons) {
                                String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                                String Name = "bluzent";
                                if (deviceName.equals(Name)) {

                                    //Toast.makeText(getActivity(), deviceName + "  ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    //android.os.Process.killProcess(android.os.Process.myPid()); // ??? ???????????? ??????
                                }
                            }
                        }

                        /**
                         *  the manager calls back this method every 1 seconds, you can get all scanned beacons.
                         *
                         *  @param minewBeacons all scanned beacons
                         */
                        @Override
                        public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                            for (MinewBeacon minewBeacon : minewBeacons) {
                                String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                                String Name = "bluzent";
                                if (deviceName.equals(Name)) {
                                    //Toast.makeText(getActivity(), deviceName + "  ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                                    //break Loop1;
                                    //android.os.Process.killProcess(android.os.Process.myPid()); // ??? ???????????? ??????
                                }
                            }

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
                                    Toast.makeText(getActivity(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                                    break;
                                case BluetoothStatePowerOff:
                                    Toast.makeText(getActivity(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });

                    //Toast.makeText(getActivity(), "Handler ??????.", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(this, 6000);
                }

            }
        }, 600);// 0.6??? ?????? ???????????? ??? ??? ??????
    } catch(
    Exception e)

    {
        Toast.makeText(getActivity(), "????????? ???????????? ??????.", Toast.LENGTH_SHORT).show();
    }


    //..

    //?????????
    mFirebaseAuth =FirebaseAuth.getInstance();
    mDatabaseRef =FirebaseDatabase.getInstance().

    getReference("bluzent");

    mBtnOnWork=v.findViewById(R.id.OnWork);

        try

    {
        mBtnOnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sendEmailToAdmin(getContext(), "??????????????? ???????????????", new String[]{"jhbyun@bluzent.com"}, androidId, Name);

            }
        });
    }catch(
    Exception e)

    {
    }

    mBtnOffWork=v.findViewById(R.id.OffWork);
    // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
        mBtnOffWork.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent intent = new Intent(Intent.ACTION_MAIN); //???????????? ??? ??????????????? ??????
        intent.addCategory(Intent.CATEGORY_HOME);   //????????? ??????
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //????????? ???????????? ???????????? ??? ?????????????????? ???????????? ??????
        startActivity(intent);
        requireActivity().startService(new Intent(getContext(), MyService.class));

        Toast.makeText(getActivity(), "??????????????? ????????? ??????", Toast.LENGTH_SHORT).show();
    }
    });

    //???????????? 10?????? ????????? ????????????
//        if (Integer.parseInt(Todaywork_Hour) >= 10) {
//            Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
//            vb.vibrate(new long[]{500, 1000, 500, 2000}, -1);
//            createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
//            createNotification(DEFAULT, 1, "??????", "??????????????? 10????????? ?????????????????????.");
//            Toast.makeText(getActivity(), "??????????????? 10????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
//        }

    //???????????? ????????????
        mDatabaseRef.child("notices").

    addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange (@NonNull DataSnapshot datasnapshot1){
            String value1 = datasnapshot1.getValue(String.class);

            if (value1 == null || value1.equals("")) {

            } else {
                createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
                createNotification(DEFAULT, 1, "????????????", value1);
            }
        }
        @Override
        public void onCancelled (@NonNull DatabaseError error){

        }
    });


    //??????????????? ??????
        Autologin.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Boolean checked = ((CheckBox) Autologin).isChecked();

        if (checked) {
            Toast.makeText(getActivity().getApplicationContext(), "?????????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            Utils.setBoolean(mContext, "checked", true);

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "?????????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            Utils.setBoolean(mContext, "checked", false);
        }


    }
    });


    //public void mOnGoHomeClick(View v){
    //Intent intent = new Intent(Intent.ACTION_MAIN); //???????????? ??? ??????????????? ??????
    //intent.addCategory(Intent.CATEGORY_HOME);   //????????? ??????
    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //????????? ???????????? ???????????? ??? ?????????????????? ???????????? ??????
    //startActivity(intent);
    //}






















        return v;
}

    private void NaverApiInit() {
        //Naver ??????
        NaverMapOptions options = new NaverMapOptions()
                .mapType(NaverMap.MapType.Basic)
                .enabledLayerGroups(NaverMap.LAYER_GROUP_TRAFFIC, NaverMap.LAYER_GROUP_TRANSIT);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(options);
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // ...
        GpsTracker gpsTracker = new GpsTracker(getActivity());
        double currentLatitude = gpsTracker.getLatitude();
        double currentLongitude = gpsTracker.getLongitude();

        Marker marker = new Marker();
        marker.setPosition(new LatLng(currentLatitude, currentLongitude));
        marker.setMap(naverMap);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(currentLatitude, currentLongitude));
        naverMap.moveCamera(cameraUpdate);
    }

    public void setTextViewValue(String str) {
        mDistanceView = (TextView) getView().findViewById(R.id.DistanceView);
        mDistanceView.setText(str); //?????? ?????? ???????????? TextView??? ????????? ??????
    }

}
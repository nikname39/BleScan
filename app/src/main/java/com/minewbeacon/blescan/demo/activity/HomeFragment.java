/**
 * work_start and end
 * by jh
 */
package com.minewbeacon.blescan.demo.activity;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.minewbeacon.blescan.demo.MyService;
import com.minewbeacon.blescan.demo.UserAccount;
import com.minewbeacon.blescan.demo.Utils;
import com.yuliwuli.blescan.demo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static int a;
    private final String DEFAULT = "DEFAULT";
    private MinewBeaconManager mMinewBeaconManager;

    // TODO: Rename and change types of parameters
    private String mParam1= "00시00분";
    private String mParam2= "00시00분";
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

    final Handler handler = new Handler();

    public HomeFragment() {
        // Required empty public constructor
    }

    void createNotificationChannel(String channelId, String channelName, int importance)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    void createNotification(String channelId, int id, String title, String text)
    {
        Drawable drawable= ContextCompat.getDrawable(mContext,R.drawable.bgs);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)          // Head Up Display를 위해 PRIORITY_HIGH 설정
                .setSmallIcon(R.drawable.bgs)        // 알림시 보여지는 아이콘. 반드시 필요
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(bitmap)
                //.setTimeoutAfter(1000)    // 지정한 시간 이후 알림 삭제
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))          // 한줄 이상의 텍스트를 모두 보여주고 싶을때 사용
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);  // 알림시 효과음, 진동 여부

        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
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

        mContext = ((MainActivity2)MainActivity2.mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //자동로그인 정보 불러오기
        CheckBox Autologin = v.findViewById(R.id.autoLogin);

        Boolean check = Utils.getBoolean(mContext, "checked");
        mCheckbox = v.findViewById(R.id.autoLogin);

        if (check == null) {
            Utils.setBoolean(mContext, "checked", false);
        } else {
            if (String.valueOf(check).equals("true")) {
                mCheckbox.setChecked(true);
            } else {
                mCheckbox.setChecked(false);
            }
        }

        String androidId = Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        //.. DAte
        mDisplayDate = (TextView) v.findViewById(R.id.tvDate);
        mOnworkView = (TextView) v.findViewById(R.id.OnworkView);
        mOffworkView = (TextView) v.findViewById(R.id.OffworkView);
        mTodayworkView = (TextView) v.findViewById(R.id.TodayworkView);

        //전날 시간 구하기
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
        String Timedate = hour + "시" + minute + "분";
        String currenttime = String.valueOf(hour*60+minute);

        //요일구분
        String dayString = "";
        switch(dayNum){
            case 1:
                dayString = "일";
                break ;
            case 2:
                dayString = "월";
                break ;
            case 3:
                dayString = "화";
                break ;
            case 4:
                dayString = "수";
                break ;
            case 5:
                dayString = "목";
                break ;
            case 6:
                dayString = "금";
                break ;
            case 7:
                dayString = "토";
                break ;
        }

        //mDisplayDate.setText(date+" "+dayString+"요일");


        mDisplayDate.setText(date);






        //출퇴근 시간 가져오기


//        mDatabaseRef.child("Attendance").child(date).child(mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").toString()).child("work_start").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
//                String value1 = datasnapshot1.getValue(String.class);
//                Work = value1;
//                mOnworkView.setText("오늘의 출근 시간:"+Work);
//                Toast.makeText(getActivity(), Work, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
//            }
//        });

        try {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if(true){



                        //전날 시간 구하기
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
                        String Timedate = hour + "시" + minute + "분";
                        String currenttime = String.valueOf(hour*60+minute);

                        //요일구분
                        String dayString = "";
                        switch(dayNum){
                            case 1:
                                dayString = "일";
                                break ;
                            case 2:
                                dayString = "월";
                                break ;
                            case 3:
                                dayString = "화";
                                break ;
                            case 4:
                                dayString = "수";
                                break ;
                            case 5:
                                dayString = "목";
                                break ;
                            case 6:
                                dayString = "금";
                                break ;
                            case 7:
                                dayString = "토";
                                break ;
                        }

                        //mDisplayDate.setText(date+" "+dayString+"요일");
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
                                Name= value;

                                //출근 시간 불러오기
                                mDatabaseRef.child("Attendance").child(date).child(Name).child("work_start").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                                        String value1 = datasnapshot1.getValue(String.class);
                                        mParam1 = value1;

                                        if (value1 == null) {
                                            //mOnworkView.setText("오늘의 출근 시간: 없음");
                                            mOnworkView.setText("오늘의 출근 시간:" + Timedate);
                                            //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                            mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_start").setValue(currenttime);
                                            mDatabaseRef.child("Attendance").child(date).child(Name).child("work_start").setValue(Timedate);
                                            mDatabaseRef.child("Attendance").child(date).child(Name).child("android_Id").setValue(androidId);
                                            mParam1 = Timedate;
                                        } else {
                                            mOnworkView.setText("오늘의 출근 시간: "+mParam1);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //퇴근 시간 불러오기
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
                                            mOffworkView.setText("마지막 근무 시간: 없음");
                                            mParam2 = Timedate;

                                        } else {
                                            //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                            mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_end").setValue(currenttime);
                                            mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").setValue(Timedate);
                                            mDatabaseRef.child("Attendance").child(date).child(Name).child("android_Id").setValue(androidId);
                                            mOffworkView.setText("마지막 근무 시간: "+Timedate);
                                        }

                                        //근무시간 구하기
                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_start").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot datasnapshot3) {
                                                String value3 = datasnapshot3.getValue(String.class);
                                                String Todaywork_Hour = String.valueOf((Integer.parseInt(currenttime) - Integer.parseInt(value3)) / 60);
                                                String Todaywork_Minutes = String.valueOf((Integer.parseInt(currenttime) - Integer.parseInt(value3)) % 60);

                                                int mintParam1 = Integer.parseInt(mParam1.substring(0, mParam1.indexOf("시")));
                                                int mintParam2 = Integer.parseInt(mParam2.substring(0, mParam2.indexOf("시")));

                                                if (value3 == null) {
                                                    mOnworkView.setText("오늘의 출근 시간: 없음");
                                                    //mOnworkView.setText("오늘의 출근 시간:" + mParam1);
                                                    //mDatabaseRef.child("Attendance").child(date).child(Name).setValue(account);
                                                    mDatabaseRef.child("Attendance").child(date).child(Name).child("Time_work_start").setValue(currenttime);
                                                    mDatabaseRef.child("Attendance").child(date).child(Name).child("work_start").setValue(Timedate);
                                                } else {

                                                    if ((mintParam1 <= 12) && (14 <= mintParam2)) {
                                                        Todaywork_Hour = String.valueOf(Integer.parseInt(Todaywork_Hour) - 1);
                                                        mOnworkView.setText("오늘의 출근 시간: " + mParam1);
                                                        mTodayworkView.setText("오늘의 근무 시간: " + Todaywork_Hour + "시간" + Todaywork_Minutes + "분");
                                                    } else {
                                                        mOnworkView.setText("오늘의 출근 시간: " + mParam1);
                                                        mTodayworkView.setText("오늘의 근무 시간: " + Todaywork_Hour + "시간" + Todaywork_Minutes + "분");
                                                    }
                                                }
                                                //근무시간 10시간 초과시 진동알람
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
                                                            createNotification(DEFAULT, 2, "알림", "근무시간이 10시간을 초과하였습니다.");
                                                            Toast.makeText(getActivity(), "근무시간이 10시간을 초과하였습니다.", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }


                                                }
                                                //익일 퇴근 체크
                                                if ((0 <= mintParam1) && (mintParam1 <= 3) && (0 <= mintParam2) && (mintParam2 <= 3)) {

                                                    try {
                                                        mDatabaseRef.child("Attendance").child(date).child(Name).child("work_end").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot datasnapshot4) {
                                                                String value4 = datasnapshot4.getValue(String.class);
                                                                String nextday = hour+24 + "시" + minute + "분";
                                                                String nextdaycurrenttime = String.valueOf(hour*60+minute+1440);

                                                                mDatabaseRef.child("Attendance").child(YesterdayDate).child(Name).child("work_end").setValue(nextday);
                                                                mDatabaseRef.child("Attendance").child(YesterdayDate).child(Name).child("Time_work_end").setValue(nextdaycurrenttime);
                                                                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                                                v.vibrate(new long[]{500, 1000, 500, 2000}, -1);
                                                                mOnworkView.setText("오늘의 출근 시간: 어제 출근");
                                                                mOffworkView.setText("마지막 근무 시간: "+Timedate);
                                                                Toast.makeText(getActivity(), "익일 퇴근으로 처리합니다.", Toast.LENGTH_SHORT).show();
                                                                mDatabaseRef.child("Attendance").child(date).child(Name).removeValue();
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }catch (Exception e) {
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
                                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                            }
                        });
                        //딜레이 후 시작할 코드 작성










                        mMinewBeaconManager = MinewBeaconManager.getInstance(mContext);
                        if (mMinewBeaconManager != null) {

                        }
                        try {
                            //mMinewBeaconManager.startScan();
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
                                for (MinewBeacon minewBeacon : minewBeacons) {
                                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                                    String Name = "bluzent";
                                    if(deviceName.equals(Name)){
                                        //Toast.makeText(getActivity(), deviceName + "  비콘 신호가 시작졌습니다.", Toast.LENGTH_SHORT).show();

                                        //break Loop1;
                                        //android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
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
                                    if(deviceName.equals(Name)){

                                        //Toast.makeText(getActivity(), deviceName + "  비콘 신호가 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                                        android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
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
                                    if(deviceName.equals(Name)){
                                        //Toast.makeText(getActivity(), deviceName + "  비콘 신호가 이어졌습니다.", Toast.LENGTH_SHORT).show();

                                        //break Loop1;
                                        //android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
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

                        //Toast.makeText(getActivity(), "Handler 반복.", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(this,6000);
                    }

                }
            }, 600);// 0.6초 정도 딜레이를 준 후 시작
        } catch (Exception e) {
            Toast.makeText(getActivity(), "데이터 불러오기 실패.", Toast.LENGTH_SHORT).show();
        }


        //..

        //출퇴근
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bluzent");

        mBtnOnWork= v.findViewById(R.id.OnWork);

        try {
            mBtnOnWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.sendEmailToAdmin(getContext(), "개발자에게 메일보내기", new String[]{"jhbyun@bluzent.com"}, androidId, Name);

                }
            });
        }catch (Exception e) {
        }

        mBtnOffWork= v.findViewById(R.id.OffWork);
        // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
        mBtnOffWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN); //태스크의 첫 액티비티로 시작
                intent.addCategory(Intent.CATEGORY_HOME);   //홈화면 표시
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
                startActivity(intent);
                requireActivity().startService(new Intent(getContext(), MyService.class));

                Toast.makeText(getActivity(), "백그라운드 서비스 시작", Toast.LENGTH_SHORT).show();
            }
        });

        //근무시간 10시간 초과시 진동알람
//        if (Integer.parseInt(Todaywork_Hour) >= 10) {
//            Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
//            vb.vibrate(new long[]{500, 1000, 500, 2000}, -1);
//            createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
//            createNotification(DEFAULT, 1, "알림", "근무시간이 10시간을 초과하였습니다.");
//            Toast.makeText(getActivity(), "근무시간이 10시간을 초과하였습니다.", Toast.LENGTH_SHORT).show();
//        }

        //공지사항 불러오기
        mDatabaseRef.child("notices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                String value1 = datasnapshot1.getValue(String.class);

                if (value1 == null || value1.equals("")) {

                } else {
                    createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
                    createNotification(DEFAULT, 1, "공지사항", value1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        //자동로그인 동작
        Autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checked = ((CheckBox) Autologin).isChecked();

                if (checked) {
                    Toast.makeText(getActivity().getApplicationContext(), "자동로그인이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    Utils.setBoolean(mContext, "checked", true);
                    mMinewBeaconManager.stopScan();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "자동로그인이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    Utils.setBoolean(mContext, "checked", false);
                }


            }
        });


        //public void mOnGoHomeClick(View v){
            //Intent intent = new Intent(Intent.ACTION_MAIN); //태스크의 첫 액티비티로 시작
            //intent.addCategory(Intent.CATEGORY_HOME);   //홈화면 표시
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
            //startActivity(intent);
        //}






















        return v;
    }
}
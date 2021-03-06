package com.bluzent.mybluzent.demo.activity.attendanceFragment;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.bluzent.mybluzent.demo.GpsTracker;
import com.bluzent.mybluzent.demo.R;
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
 * Use the {@link Home2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mBtnOpenDoor, mBtnCloseDoor;
    private TextView mDoorCheck;
    private ImageView mDoorCheckImage;


    public static Context mContext;

    static String MQTTHOST = "tcp://3.38.101.34:1883";;
    static String USERNAME = "ebluzent_sub1";
    static String PASSWORD = "1234";

    MqttAndroidClient client;

    public Home2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home2.
     */
    // TODO: Rename and change types and number of parameters
    public static Home2 newInstance(String param1, String param2) {
        Home2 fragment = new Home2();
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
        View v = inflater.inflate(R.layout.fragment_home2, container, false);



        mDoorCheck = v.findViewById(R.id.DoorCheck);
        mDoorCheckImage = v.findViewById(R.id.DoorCheckImage);
        mBtnOpenDoor= v.findViewById(R.id.button3);

        MqttInit();
        MqttSubCallback();

        //????????? ?????? ??????
        mBtnOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttPub("SongDo/DoorLock1", "1");

            }
        });
        return v;
    }



    //Mqtt ?????? ??????
    private void MqttInit() {
        //MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getActivity(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getActivity(), "connected", Toast.LENGTH_SHORT).show();
                    try {

                        client.subscribe("SongDo/DoorLock1", 0 );   //????????? ???????????? SongDo/DoorLock1 ?????? ???????????? subscribe???

                    } catch (MqttException e) {

                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getActivity(), "connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    //Mqtt Sub ??????
    private void MqttSubCallback() {
        client.setCallback(new MqttCallback() {  //?????????????????? ????????? ??????????????????

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {    //?????? ???????????? ?????? Callback method
                if (topic.equals("SongDo/DoorLock1")){     //topic ?????? ?????????????????? ????????? ?????????????????????
                    String msg = new String(message.getPayload());
                    //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    if (msg.equals("1")){
                        mDoorCheck.setText("??????");
                        mDoorCheckImage.setImageResource(R.drawable.padlockopen);
                    }
                    else{
                        mDoorCheck.setText("??????");
                        mDoorCheckImage.setImageResource(R.drawable.padlock);
                    }

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    //Mqtt Pub ??????
    private void sendMqttPub(String pubTopic, String pubMessage) {
        String topic = pubTopic;
        String message = pubMessage;
        try {
            client.publish(topic, message.getBytes(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "5??? ??? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
    }

}
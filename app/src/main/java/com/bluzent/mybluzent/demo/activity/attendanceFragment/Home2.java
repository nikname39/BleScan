package com.bluzent.mybluzent.demo.activity.attendanceFragment;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.bluzent.mybluzent.demo.R;
import com.bluzent.mybluzent.demo.activity.MainActivity2;



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

        //도어락 열기 버튼
        mBtnOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttPub("SongDo/DoorLock1", "1");

            }
        });
        return v;
    }

    //Mqtt 연결 시작
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

                        client.subscribe("SongDo/DoorLock1", 0 );   //연결에 성공하면 SongDo/DoorLock1 라는 토픽으로 subscribe함

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

    //Mqtt Sub 시작
    private void MqttSubCallback() {
        client.setCallback(new MqttCallback() {  //클라이언트의 콜백을 처리하는부분

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {    //모든 메시지가 올때 Callback method
                if (topic.equals("SongDo/DoorLock1")){     //topic 별로 분기처리하여 작업을 수행할수도있음
                    String msg = new String(message.getPayload());
                    //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    if (msg.equals("1")){
                        mDoorCheck.setText("열림");
                        mDoorCheckImage.setImageResource(R.drawable.padlockopen);
                    }
                    else{
                        mDoorCheck.setText("닫힘");
                        mDoorCheckImage.setImageResource(R.drawable.padlock);
                    }

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    //Mqtt Pub 시작
    private void sendMqttPub(String pubTopic, String pubMessage) {
        String topic = pubTopic;
        String message = pubMessage;
        try {
            client.publish(topic, message.getBytes(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "5초 후 자동으로 닫힙니다.", Toast.LENGTH_SHORT).show();
    }

}
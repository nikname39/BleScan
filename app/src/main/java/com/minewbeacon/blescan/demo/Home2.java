package com.minewbeacon.blescan.demo;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.yuliwuli.blescan.demo.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

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
    private WebView wView; // 웹뷰


    static String MQTTHOST = "tcp://3.38.101.34:1883";;
    static String USERNAME = "ebluzent_sub1";
    static String PASSWORD = "1234";
    String pubTopic = "SongDo/DoorLock1";

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home2, container, false);

        WebView webView = v.findViewById(R.id.wView);

        //MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getActivity(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            //IMqttToken token = client.connect();
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getActivity(), "connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getActivity(), "connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        mBtnOpenDoor= v.findViewById(R.id.button3);
        mBtnOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String topic = pubTopic;
                String message = "1";
                try {
                    client.publish(topic, message.getBytes(), 2, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "5초 후 자동으로 닫힙니다.", Toast.LENGTH_SHORT).show();

            }
        });

//        mBtnCloseDoor= v.findViewById(R.id.button4);
//        // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
//        mBtnCloseDoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                webView.getSettings().setJavaScriptEnabled(true);
//                webView.setWebViewClient(new WebViewClient());
//                webView.loadUrl("http://172.30.1.2/H");
//
//            }
//        });


        return v;
    }
}
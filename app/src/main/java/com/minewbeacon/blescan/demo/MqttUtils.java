package com.minewbeacon.blescan.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.yuliwuli.blescan.demo.BuildConfig;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttUtils {

    static String MQTTHOST = "tcp://3.38.101.34:1883";
    static String USERNAME = "ebluzent_sub1";
    static String PASSWORD = "1234";

    public static String pubTopic = "SongDo/DoorLock1";
    public static String pubMessage = "1";

    static MqttAndroidClient client;

    static Context mContext;

    public MqttUtils(Context context){
        mContext = context;

        //MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(mContext, MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(mContext, "connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(mContext, "connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public static void sendMqttPub(String pubTopic, String pubMessage){



        String topic = pubTopic;
        String message = pubMessage;
        try {
            client.publish(topic, message.getBytes(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, "5초 후 자동으로 닫힙니다.", Toast.LENGTH_SHORT).show();
    }

    public static void testMqtt(Context context){
        //MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(context, "connection failed", Toast.LENGTH_SHORT).show();
            Log.e("스테이트 상태", "1111111111111");
        }
    }

    public static void test(Context context){
        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
    }

}


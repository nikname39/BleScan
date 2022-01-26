package com.minewbeacon.blescan.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.yuliwuli.blescan.demo.BuildConfig;

public class EmailUtils {
    public static void sendEmailToAdmin(Context context, String title, String[] receivers,String android_id, String name){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, title);
        email.putExtra(Intent.EXTRA_EMAIL, receivers);
        email.putExtra(Intent.EXTRA_TEXT, String.format("App Version : %s\nDevice : %s\nName : %s\nAndroid(SDK) : %d(%s)\n내용 : ", BuildConfig.VERSION_NAME, android_id, name, Build.VERSION.SDK_INT, Build.VERSION.RELEASE));
        email.setType("message/rfc822");
        context.startActivity(email);
    }
}
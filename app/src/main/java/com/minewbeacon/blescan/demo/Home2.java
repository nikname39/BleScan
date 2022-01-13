package com.minewbeacon.blescan.demo;

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

import com.yuliwuli.blescan.demo.R;

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

        mBtnOpenDoor= v.findViewById(R.id.button3);
        // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
        mBtnOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("http://172.30.1.2/L");
                webView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN){
                            if (keyCode == KeyEvent.KEYCODE_BACK){

                                if (webView!=null){
                                    if (webView.canGoBack()){
                                        webView.goBack();
                                    }else {
                                        getActivity().onBackPressed();
                                    }
                                }
                            }
                        }return true;
                    }
                });
            }
        });

        mBtnCloseDoor= v.findViewById(R.id.button4);
        // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
        mBtnCloseDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("http://172.30.1.2/H");
                webView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN){
                            if (keyCode == KeyEvent.KEYCODE_BACK){

                                if (webView!=null){
                                    if (webView.canGoBack()){
                                        webView.goBack();
                                    }else {
                                        getActivity().onBackPressed();
                                    }
                                }
                            }
                        }return true;
                    }
                });
            }
        });


        return v;
    }
}
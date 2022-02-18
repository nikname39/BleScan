package com.bluzent.mybluzent.demo.activity.attendanceFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import com.bluzent.mybluzent.demo.R;
import com.bluzent.mybluzent.demo.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Switch mSwitch;

    public Home3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home3.
     */
    // TODO: Rename and change types and number of parameters
    public static Home3 newInstance(String param1, String param2) {
        Home3 fragment = new Home3();
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
        View v = inflater.inflate(R.layout.fragment_home3, container, false);

        //자동로그인 정보 불러오기
        Boolean check = Utils.getBoolean(getActivity(), "checked");
        mSwitch =v.findViewById(R.id.switch1);

        if(check ==null)

        {
            Utils.setBoolean(getActivity(), "checked", false);
        } else

        {
            if (String.valueOf(check).equals("true")) {
                mSwitch.setChecked(true);
            } else {
                mSwitch.setChecked(false);
            }
        }

        //자동로그인 동작
        mSwitch.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                Boolean checked = ((Switch) mSwitch).isChecked();

                if (checked) {
                    Toast.makeText(getActivity().getApplicationContext(), "자동로그인이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    Utils.setBoolean(getActivity(), "checked", true);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "자동로그인이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    Utils.setBoolean(getActivity(), "checked", false);
                }


            }
        });

        return v;

    }

    private void AutoLogin() {

    }
}
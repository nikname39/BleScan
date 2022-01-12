package com.minewbeacon.blescan.demo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuliwuli.blescan.demo.R;

public class Register extends AppCompatActivity {

    private RegisterDAO dao;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd, mEtName;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bluzent");

        mEtEmail = findViewById(R.id.et_email);
        //mEtPwd = findViewById(R.id.et_pwd);
        mEtName = findViewById(R.id.et_name);
        mBtnRegister= findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String strEmail = mEtEmail.getText().toString();
                //String strPwd = mEtPwd.getText().toString();
                String strName = mEtName.getText().toString();

                try {
                    //출근 시간 불러오기
                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                    UserAccount account = new UserAccount();  //VO
                    //account.setIdToken(firebaseUser.getUid());
                    //account.setEmailId(firebaseUser.getEmail());
                    account.setEmailId(strEmail);
                    //account.setPassword(strPwd);
                    account.setName(strName);
                    account.setAndroid_Id(androidId);

                    mDatabaseRef.child("UserAccount").child(androidId).child("android_Id").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                            String value1 = datasnapshot1.getValue(String.class);

                            if (value1 == null) {
                                mDatabaseRef.child("UserAccount").child(androidId).setValue(account);

                                Toast.makeText(Register.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(Register.this, "회원가입 실패. 기기 하나당 하나의 아이디만 가입가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //이메일로그인방식
                    //mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
//                    mFirebaseAuth.child("Attendance").child(date).child(Name).child("work_end").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot datasnapshot2) {
//                            if(task.isSuccessful()){
//                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
//
//                                UserAccount account = new UserAccount();  //VO
//                                account.setIdToken(firebaseUser.getUid());
//                                account.setEmailId(firebaseUser.getEmail());
//                                account.setPassword(strPwd);
//                                account.setName(strName);
//                                account.setAndroid_Id(androidId);
//                                //account.setName(firebaseUser.getDisplayName());
//
//                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
//
//                                Toast.makeText(Register.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                                startActivity(intent);
//                                finish();
//                            }else {
//                                Toast.makeText(Register.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

                } catch (Exception e) {
                    Toast.makeText(Register.this, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


            }


        });

    }


}
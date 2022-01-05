/**
 * register
 * by ej
 */
package com.minewbeacon.blescan.demo;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterDAOImpl extends AppCompatActivity implements RegisterDAO{

    private DatabaseReference mDatabase;

    @Override
    public FirebaseUser getUser() throws Exception {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public DatabaseReference getDatabase() throws Exception{
        return FirebaseDatabase.getInstance().getReference("bluzent");
    }

    @Override
    public FirebaseAuth getAuth() throws Exception {
        return FirebaseAuth.getInstance();
    }
}
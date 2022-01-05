package com.minewbeacon.blescan.demo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public interface RegisterDAO {

    public FirebaseUser getUser() throws Exception;
    public DatabaseReference getDatabase() throws Exception;
    public FirebaseAuth getAuth() throws Exception;

}

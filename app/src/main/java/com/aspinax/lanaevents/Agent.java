package com.aspinax.lanaevents;

import com.google.firebase.Timestamp;

public class Agent extends Person {
    public Agent() {}
    public Agent(String fName, String lName, String email, String password, String mNumber, Timestamp created, Integer access, String organization, String phoneNumber, String role) {
        super(fName, lName, mNumber, email, password, created, access, phoneNumber, organization, role);
    }
}
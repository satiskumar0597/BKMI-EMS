package com.aspinax.lanaevents;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Person {
    public String fName, lName, mNumber, email, password, organization, phoneNumber, role;
    public Integer access;
    public Timestamp created;
    private FirebaseAuth auth;
    private Database db = new Database(new AsyncResponse() {
        @Override
        public void resultHandler(Map<String, Object> result, int resultCode) { }

        @Override
        public void resultHandler(String msg, int resultCode) {

        }
    });

    public Person() {}
    public Person(String fName, String lName, String mNumber, String email, String password, Timestamp created, Integer access, String phoneNumber, String organization, String role) {
        this.fName = fName;
        this.lName = lName;
        this.mNumber = mNumber;
        this.email = email;
        this.password = password;
        this.created = created;
        this.access = access;
        this.organization = organization;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) { this.password = password; }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setMatric(String mNumber) {
        this.mNumber = mNumber;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateProfile(Map<String, Object> data) {
        this.db.update("users", this.auth.getUid(), data, 0);
    }

    String getFullName() {
        return fName + " " + lName;
    }

    static void saveUser(FirebaseAuth auth, String fName, String lName, String mNumber, String email, String password, String phoneNumber, String orgName, FieldValue created, String role) {
        FirebaseUser user = auth.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("fName", fName);
        data.put("lName", lName);
        data.put("mNumber", mNumber);
        data.put("email", email);
        data.put("password", password);
        data.put("created", created);
        data.put("access", 0);
        data.put("phoneNumber", phoneNumber);
        data.put("organization", orgName);
        data.put("role", role);

        Database db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) { }

            @Override
            public void resultHandler(String msg, int resultCode) {

            }
        });
        assert user != null;
        db.set("users", user.getUid(), data, 0);
    }
}
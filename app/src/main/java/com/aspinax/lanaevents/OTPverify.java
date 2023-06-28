package com.aspinax.lanaevents;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;


public class OTPverify extends AppCompatActivity {

    private static final String TAG = "OTPverify classs";
    // variable for FirebaseAuth class
    private FirebaseAuth mAuth;

    // get document snapshot of user for their phone number
    private DatabaseReference databasereference;

    // variable for our text input
    // field for phone and OTP.
    private EditText edtOTP;
    private TextView edtPhone;

    // string for storing our verification ID
    private String verificationId;
    private String phoneNum;

    // variables for previous value
    private String fName, lName, mNumber, phoneNumber, role, email, password, orgName, documentID;

    private boolean userExist= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // buttons for generating OTP and verifying OTP
        final Button verifyOTPBtn, generateOTPBtn;

        // below line is for getting instance
        // of our FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        databasereference = FirebaseDatabase.getInstance().getReference();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // initializing variables for button and Edittext.
        edtOTP = findViewById(R.id.otp);
        edtPhone = findViewById(R.id.textView4);
        verifyOTPBtn = findViewById(R.id.idBtnVerify);
        generateOTPBtn = findViewById(R.id.idBtnGetOtp);

        // get the data placed in previous intent to add into database
        Intent intent = getIntent();
        fName = intent.getStringExtra("fName");
        lName = intent.getStringExtra("lName");
        mNumber = intent.getStringExtra("mNumber");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        phoneNumber = intent.getStringExtra("phoneNumber");
        orgName = intent.getStringExtra("orgName");
        role = intent.getStringExtra("role");
        documentID = intent.getStringExtra("documentID");

        edtPhone.setText(phoneNumber);

        CheckUserExistence(documentID);
        // get phone number to send otp
//        DocumentReference docRef = db.collection("users").document(mAuth.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document != null) {
//                        Log.i("LOGGER", "First " + document.getString("phoneNumber"));
//                        phoneNum = document.getString("phoneNumber");
//                        edtPhone.setText(phoneNum);
//
//                    } else {
//                        Log.d("LOGGER", "No such document");
//                        edtPhone.setText(phoneNum);
//                    }
//                } else {
//                    Log.d("LOGGER", "get failed with ", task.getException());
//                }
//            }
//        });


        // setting onclick listener for generate OTP button.
        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is for checking whether the user
                // has entered his mobile number or not.
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    Toast.makeText(OTPverify.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    String phone = "+6" + edtPhone.getText().toString();
                    sendVerificationCode(phone);
                }
            }
        });

        // initializing on click listener
        // for verify otp button
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(edtOTP.getText().toString())) {
                    // if the OTP text field is empty display
                    // a message to user to enter OTP
                    Toast.makeText(OTPverify.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    // if OTP field is not empty calling
                    // method to verify the OTP.
                    verifyCode(edtOTP.getText().toString());
                }
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)    //signinAnonomously()
                .addOnCompleteListener(OTPverify.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(userExist==false){
                                // if verification success, add user
                                Person.saveUser(mAuth, fName, lName, mNumber, email, password, phoneNumber, orgName, FieldValue.serverTimestamp(), role);

                                if(role.matches("Admin")){
                                    Intent i = new Intent(OTPverify.this, AdminMainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);}
                                else{
                                    Intent i = new Intent(OTPverify.this, StudentMainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);}

                            }
                            else{
                                // if the code is correct and the task is successful
                                // we are sending our user to new activity.
                                if(role.matches("Admin")){
                                    Intent i = new Intent(OTPverify.this, AdminMainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }
                                else{
                                    Intent i = new Intent(OTPverify.this, StudentMainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }
                            }
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(OTPverify.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

//    private void signInWithCredential() {
//        // inside this method we are checking if
//        // the code entered is correct or not.
//        mAuth.signInAnonymously()//()signInWithCredential(credential)
//                .addOnCompleteListener(OTPverify.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            if(CheckUserExistence()==false){
//                                // if verification success, add user
//                                Person.saveUser(mAuth, fName, lName, mNumber, email, phoneNumber, orgName, FieldValue.serverTimestamp(), role);
//
//                                if(role.matches("Admin")){
//                                    Intent i = new Intent(OTPverify.this, AdminMainActivity.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(i);}
//                                else{
//                                    Intent i = new Intent(OTPverify.this, StudentMainActivity.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(i);}
//
//                            }
//                            else{
//                                // if the code is correct and the task is successful
//                                // we are sending our user to new activity.
//                                if(role.matches("Admin")){
//                                    Intent i = new Intent(OTPverify.this, AdminMainActivity.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(i);
//                                }
//                                else{
//                                    Intent i = new Intent(OTPverify.this, StudentMainActivity.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(i);
//                                }
//                            }
//                        } else {
//                            // if the code is not correct then we are
//                            // displaying an error message to the user.
//                            Toast.makeText(OTPverify.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }


    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)         // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)         // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                edtOTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(OTPverify.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
        //signInWithCredential();
    }

    private void CheckUserExistence(String docuID) {
        //get the user id
        if (docuID == null)
            docuID = "0000";
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("users").document(docuID);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        userExist = true;
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        userExist = false;
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}



package com.aspinax.lanaevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Database db;
    // variables for previous value
    private String fName, lName, mNumber, phoneNumber, role, email, password, orgName, documentID;
    private boolean validusername=false, validpassword=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                if (resultCode == 0 && validusername==true && validpassword==true) {
//                    Person p = (Person) result.get("documentID");
//                    Intent intent;
//                    assert p != null;
//                    if (p.access > 0)
//                        intent = new Intent(LoginActivity.this, StudentMainActivity.class);
//                    else{
                    Intent intent = new Intent(LoginActivity.this, OTPverify.class);
                    intent.putExtra("fName", result.get("fName").toString());
                    intent.putExtra("lName",result.get("lName").toString());
                    intent.putExtra("mNumber", result.get("mNumber").toString());
                    intent.putExtra("email", result.get("email").toString());
                    intent.putExtra("phoneNumber", result.get("phoneNumber").toString());
                    intent.putExtra("organization", result.get("organization").toString());
                    intent.putExtra("role", result.get("role").toString());
                    intent.putExtra("documentID", result.get("documentID").toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    final TextInputEditText emailView = findViewById(R.id.email);
                    final TextInputEditText passwordView = findViewById(R.id.password);
                    Toast.makeText(getApplicationContext(),"Invalid User Credentials !", Toast.LENGTH_LONG).show();
                    emailView.setText("");
                    passwordView.setText("");
                }
            }

            @Override
            public void resultHandler(String msg, int resultCode) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        final MaterialButton login_btn = findViewById(R.id.login_btn);
        final TextInputEditText emailView = findViewById(R.id.email);
        final TextInputEditText passwordView = findViewById(R.id.password);
        final TextView forgotpass = findViewById(R.id.forgot_pass);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Objects.requireNonNull(emailView.getText()).toString().trim();
                final String password = Objects.requireNonNull(passwordView.getText()).toString().trim();

                verifyEmailPassword(email, password);

                if (isValidEmail(email)){
                    if (isValidPassword(password)){
                        db.getLoginDocument(email, Database.class, 0);
                    }
                    else{
                        passwordView.setError("Invalid User Password");
                    }
                }
                else{
                    emailView.setError("Invalid User Credentials");
                }
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPassword(CharSequence password) {
        return (!TextUtils.isEmpty(password) && password.length() > 8);
    }


    private void verifyEmailPassword(String Email, String Password){
        FirebaseFirestore db1=FirebaseFirestore.getInstance();

        final TextInputEditText emailView = findViewById(R.id.email);
        final TextInputEditText passwordView = findViewById(R.id.password);

        db1.collection("users")
                .whereEqualTo("email", Email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println("-------2---------"+document.getId());
                                System.out.println("-------2---------"+document.getData());

                                if(document.get("email").toString().matches(Email)) {
                                    validusername = true;
                                    if (document.get("password").toString().matches(Password)){
                                        validpassword = true;
                                    }
                                    else{
                                        validpassword = false;
                                        Toast.makeText(getApplicationContext(),"Invalid User Credentials !", Toast.LENGTH_LONG).show();
                                        emailView.setText("");
                                        passwordView.setText("");
                                    }
                                }
                            }
                        }
                        else {
                            Log.d(TAG, "Email Address Not Found  ", task.getException());
                        }
                    }
                });
    }
}
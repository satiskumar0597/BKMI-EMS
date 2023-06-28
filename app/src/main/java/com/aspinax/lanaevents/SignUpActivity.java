package com.aspinax.lanaevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;

import java.util.Objects;

import static com.aspinax.lanaevents.LoginActivity.isValidEmail;
import static com.aspinax.lanaevents.LoginActivity.isValidPassword;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final MaterialButton sign_up_btn = findViewById(R.id.sign_up_btn);
        final TextInputEditText fNameView = findViewById(R.id.fName);
        final TextInputEditText lNameView = findViewById(R.id.lName);
        final TextInputEditText matricView = findViewById(R.id.mNumber);
        final TextInputEditText emailView = findViewById(R.id.email);
        final TextInputEditText passwordView = findViewById(R.id.password);
        final TextInputEditText phoneNumberView = findViewById(R.id.phoneNumber);
        final TextInputEditText orgView = findViewById(R.id.organizationName);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fName = Objects.requireNonNull(fNameView.getText()).toString().trim();
                final String lName = Objects.requireNonNull(lNameView.getText()).toString().trim();
                final String mNumber = Objects.requireNonNull(matricView.getText()).toString().trim();
                final String email = Objects.requireNonNull(emailView.getText()).toString().trim();
                final String password = Objects.requireNonNull(passwordView.getText()).toString().trim();
                final String phoneNumber = Objects.requireNonNull(phoneNumberView.getText()).toString().trim();
                final String orgName = Objects.requireNonNull(orgView.getText()).toString().trim();
                final String role = intent.getStringExtra("role");

                if (TextUtils.isEmpty(fName)) {
                    fNameView.setError(getString(R.string.fname_val));
                    return;
                }
                if (TextUtils.isEmpty(lName)) {
                    lNameView.setError("Please enter your last name.");
                    return;
                }
                if (TextUtils.isEmpty(mNumber)) {
                    matricView.setError("Please enter your matric number.");
                    return;
                }
                if (!isValidEmail(email)) {
                    emailView.setError(getString(R.string.email_val));
                    return;
                }
                if (!isValidPassword(password)) {
                    passwordView.setError(getString(R.string.password_val));
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberView.setError("Please provide a phone number.");
                    return;
                }
                if (TextUtils.isEmpty(orgName)) {
                    orgView.setError("Please provide an organization name.");
                    return;
                }

//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    //Person.saveUser(mAuth, fName, lName, mNumber, email, phoneNumber, orgName, FieldValue.serverTimestamp(), role);
//                                    Intent intent = new Intent(SignUpActivity.this, OTPverify.class);
//                                    intent.putExtra("fName", fName);
//                                    intent.putExtra("lName",lName);
//                                    intent.putExtra("mNumber",mNumber);
//                                    intent.putExtra("email",email);
//                                    intent.putExtra("phoneNumber",phoneNumber);
//                                    intent.putExtra("orgName",orgName);
//                                    intent.putExtra("role",role);
//
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                } else {
//                                    Toast.makeText(getApplicationContext(), getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

                        Intent intent = new Intent(SignUpActivity.this, OTPverify.class);
                                    intent.putExtra("fName", fName);
                                    intent.putExtra("lName",lName);
                                    intent.putExtra("mNumber",mNumber);
                                    intent.putExtra("email",email);
                                    intent.putExtra("password",password);
                                    intent.putExtra("phoneNumber",phoneNumber);
                                    intent.putExtra("orgName",orgName);
                                    intent.putExtra("role",role);
                                    intent.putExtra("documentID",mAuth.getUid());

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
            }
        });
    }
}
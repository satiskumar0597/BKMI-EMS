package com.aspinax.lanaevents;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.aspinax.lanaevents.LoginActivity.isValidEmail;
import static com.aspinax.lanaevents.LoginActivity.isValidPassword;


public class ProfileFragment extends Fragment {
    private Database db;
    private FirebaseAuth mAuth;
    private Person p;
    private boolean tokenExist;
    private MaterialButton book, clearButton;
    private TextInputEditText userEmail;
    private TextInputEditText generatedKey;
    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextInputEditText fNameView = view.findViewById(R.id.fName);
        final TextInputEditText lNameView = view.findViewById(R.id.lName);
        final TextInputEditText emailView = view.findViewById(R.id.email);
        final TextInputEditText phoneView = view.findViewById(R.id.phoneNumber);
        final MaterialButton saveContact = view.findViewById(R.id.saveContact);
        final MaterialButton saveName = view.findViewById(R.id.saveName);
        book = view.findViewById(R.id.genButton);
        clearButton = view.findViewById(R.id.clearButton);
        userEmail = view.findViewById(R.id.useremail);
        generatedKey = view.findViewById(R.id.generatedkey);

        db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                if (resultCode == 0) {
                    p = (Person) result.get(mAuth.getUid());
                    assert p != null;
                    fNameView.setText(p.fName);
                    fNameView.setEnabled(true);
                    lNameView.setText(p.lName);
                    lNameView.setEnabled(true);
                    saveName.setEnabled(true);
                    emailView.setText(p.email);
                    emailView.setEnabled(true);
                    phoneView.setText(p.phoneNumber);
                    phoneView.setEnabled(true);
                    saveContact.setEnabled(true);
                    userEmail.setEnabled(true);
                    generatedKey.setEnabled(true);
                } else if (resultCode == 1) {
                    Toast.makeText(getContext(), "Profile Updated.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void resultHandler(String msg, int resultCode) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        db.read("users", mAuth.getUid(), Agent.class, 0);

        MaterialButton logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), NoAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        saveName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String fName = Objects.requireNonNull(fNameView.getText()).toString().trim();
                String lName = Objects.requireNonNull(lNameView.getText()).toString().trim();

                if (TextUtils.isEmpty(fName)) {
                    fNameView.setError(getString(R.string.fname_val));
                    return;
                }
                if (TextUtils.isEmpty(lName)) {
                    lNameView.setError("Please enter your last name.");
                    return;
                }

                Map<String, Object> data = new HashMap<>();
                data.put("fName", fName);
                data.put("lName", lName);
                db.update("users", mAuth.getUid(), data, 1);
            }
        });

        saveContact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(emailView.getText()).toString().trim();
                String phoneNumber = Objects.requireNonNull(phoneView.getText()).toString().trim();

                if (!isValidEmail(email)) {
                    emailView.setError(getString(R.string.email_val));
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneView.setError(getString(R.string.password_val));
                    return;
                }

                Map<String, Object> data = new HashMap<>();
                data.put("email", email);
                data.put("phoneNumber", phoneNumber);
                db.update("users", mAuth.getUid(), data, 1);
            }
        });

        book.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                genKey(book);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { markAsUnbooked(book);}
        });

        final TextInputEditText oldPasswordView = view.findViewById(R.id.oldPassword);
        final TextInputEditText newPasswordView = view.findViewById(R.id.newPassword);
        MaterialButton savePassword = view.findViewById(R.id.savePassword);
        savePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String oldPassword = Objects.requireNonNull(oldPasswordView.getText()).toString().trim();
                String newPassword = Objects.requireNonNull(newPasswordView.getText()).toString().trim();

                if (!isValidPassword(oldPassword)) {
                    oldPasswordView.setError(getString(R.string.password_val));
                    return;
                }
                if (!isValidPassword(newPassword)) {
                    newPasswordView.setError(getString(R.string.password_val));
                    return;
                }

                // update password
            }
        });
        return view;
    }
    private void markAsBooked(MaterialButton book) {
        book.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        book.setText("Key Generated");
    }

    private void markAsUnbooked(MaterialButton book){
        book.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
        book.setText("Generate");
        generatedKey.setText("");
        userEmail.setText("");
    }

    private void genKey(MaterialButton book){
        //book.setEnabled(true);
        //book.setOnClickListener(v -> {
                 if (verifyNokey(String.valueOf(userEmail.getText())) == false)
                     insertNewkey(genEventID());

                Toast.makeText(getContext(), "The token successfully generated", Toast.LENGTH_LONG).show();
                markAsBooked(book);

        //});
    }

    public String genEventID(){
        String key = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        StringBuilder genID = new StringBuilder(15);

        for (int i=0; i<15; i++){
            int index = (int)(key.length()* Math.random());
            genID.append(key.charAt(index));
        }
        generatedKey.setText(genID.toString());
        return genID.toString();
    }

    public void insertNewkey(String userToken){
        Map<String,Object> record = new HashMap<>();
        record.put("token", userToken);
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("userToken").document(userEmail.getText().toString()).set(record);
    }
    private boolean verifyNokey(String Email){

            FirebaseFirestore db1=FirebaseFirestore.getInstance();

            db1.collection("userToken")
                    .whereEqualTo("email", Email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    if(document.get("email").toString().matches(Email)) {
                                        tokenExist = true;
                                    }
                                    else
                                        tokenExist = false;
                                }
                            }
                            else {
                                Toast.makeText(getContext(), "Key already generated for this user", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    return tokenExist;
        }

}


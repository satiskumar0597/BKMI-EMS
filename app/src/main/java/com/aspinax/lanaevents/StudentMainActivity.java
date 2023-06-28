package com.aspinax.lanaevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class StudentMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Database db;
    //private Person p;
    private TextView greeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        greeting = findViewById(R.id.greeting);

        mAuth = FirebaseAuth.getInstance();
        Log.d("IF THERE IS CURRENT student USER : ", String.valueOf(mAuth.getCurrentUser()));
        System.out.println("IF THERE IS CURRENT student USER ---------------------------------------------------: " + mAuth.getCurrentUser().getUid());
        getStudentName();

        db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                if (resultCode == 0) {
                    Person p = (Person) result.get(mAuth.getUid());
                    System.out.println("CURRENT student USER ID AT LINE 41--------------------------------------------------------: " + mAuth.getUid());
                    assert p != null;
                    if (p.access > 0) {
//                        Intent intent = new Intent(StudentMainActivity.this, AdminMainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    }
                    p.setAuth(mAuth);
                }
            }

            @Override
            public void resultHandler(String msg, int resultCode) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        setDiscoverFragment();
        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.discover:
                        setDiscoverFragment();
                        getStudentName();
                        break;
                    case R.id.myevents:
                        //UserEventsFragment eventsFragment = new UserEventsFragment();
                        StudentEventsFragment eventsFragment = new StudentEventsFragment();
                        fragmentTransaction.replace(R.id.content, eventsFragment);
                        fragmentTransaction.commit();
                        greeting.setText("");
                        break;
                    case R.id.profile:
                        StudentProfileFragment profileFragment = new StudentProfileFragment();
                        fragmentTransaction.replace(R.id.content, profileFragment);
                        fragmentTransaction.commit();
                        greeting.setText("");
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), NoAuthActivity.class));
            finish();
        } else {
            db.read("users", currentUser.getUid(), Person.class, 0);
        }
    }

    private void setDiscoverFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        StudentDiscoverFragment discoverFragment = new StudentDiscoverFragment();
        fragmentTransaction.replace(R.id.content, discoverFragment);
        fragmentTransaction.commit();
    }

    public static Bitmap decodeBase64(String base64String) {
        base64String = base64String.substring(base64String.indexOf(",") + 1);
        final byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void getStudentName(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
        DocumentReference docRef = dbInstance.collection("users").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        greeting.setText("Hi, " + document.getString("fName"));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
    }
}

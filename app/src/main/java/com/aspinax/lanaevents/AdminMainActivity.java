package com.aspinax.lanaevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Database db;
    //private Person p;

    private FloatingActionButton eventFloat, addEventFloat, viewReportFloat;
    private TextView add_event_report, view_report_float, greeting;
    private View bckgroundDimmer;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        bckgroundDimmer = findViewById(R.id.background_dimmer);
        eventFloat = findViewById(R.id.add_fab);
        addEventFloat = findViewById(R.id.addEventFloat);
        greeting = findViewById(R.id.greeting);
        viewReportFloat = findViewById(R.id.viewReportFloat);

        add_event_report = findViewById(R.id.add_event_float);
        view_report_float = findViewById(R.id.view_report_float);

        addEventFloat.hide();
        viewReportFloat.hide();
        add_event_report.setVisibility(View.GONE);
        view_report_float.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        Log.d("IF THERE IS CURRENT USER : ", String.valueOf(mAuth.getCurrentUser()));
        System.out.println("IF THERE IS CURRENT USER ---------------------------------------------------: " + mAuth.getCurrentUser().getUid());
        getAdminName();
        db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                if (resultCode == 0) {
                    Person p = (Person) result.get(mAuth.getUid());
                    System.out.println("CURRENT USER ID AT LINE 41--------------------------------------------------------: " + mAuth.getUid());
                    assert p != null;
                    if (p.access > 0) {
                        Intent intent = new Intent(AdminMainActivity.this, StudentMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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
                        addEventFloat.hide();
                        viewReportFloat.hide();
                        bckgroundDimmer.setVisibility(View.GONE);
                        getAdminName();
                        add_event_report.setVisibility(View.GONE);
                        view_report_float.setVisibility(View.GONE);
                        setDiscoverFragment();
                        break;
                    case R.id.myevents:
                        addEventFloat.hide();
                        viewReportFloat.hide();
                        bckgroundDimmer.setVisibility(View.GONE);
                        add_event_report.setVisibility(View.GONE);
                        view_report_float.setVisibility(View.GONE);
                        //UserEventsFragment eventsFragment = new UserEventsFragment();
                        AdminEventsFragment eventsFragment = new AdminEventsFragment();
                        fragmentTransaction.replace(R.id.content, eventsFragment);
                        fragmentTransaction.commit();
                        greeting.setText("");
                        break;
                    case R.id.profile:
                        addEventFloat.hide();
                        viewReportFloat.hide();
                        bckgroundDimmer.setVisibility(View.GONE);
                        add_event_report.setVisibility(View.GONE);
                        view_report_float.setVisibility(View.GONE);
                        ProfileFragment profileFragment = new ProfileFragment();
                        fragmentTransaction.replace(R.id.content, profileFragment);
                        fragmentTransaction.commit();
                        greeting.setText("");
                        break;
                }
                return true;
            }
        });

        // open up the floating button when clicked
        eventFloat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isAllFabsVisible) {
                    // when isAllFabsVisible becomes true make all
                    // the action name texts and FABs VISIBLE
                    bckgroundDimmer.setBackgroundColor(Color.WHITE);
                    bckgroundDimmer.setVisibility(View.VISIBLE);
                    addEventFloat.show();
                    viewReportFloat.show();
                    add_event_report.setVisibility(View.VISIBLE);
                    view_report_float.setVisibility(View.VISIBLE);

                    // make the boolean variable true as we
                    // have set the sub FABs visibility to GONE
                    isAllFabsVisible = true;
                } else {
                    // when isAllFabsVisible becomes true make
                    // all the action name texts and FABs GONE.
                    bckgroundDimmer.setVisibility(View.GONE);
                    addEventFloat.hide();
                    viewReportFloat.hide();
                    add_event_report.setVisibility(View.GONE);
                    view_report_float.setVisibility(View.GONE);

                    // make the boolean variable false as we
                    // have set the sub FABs visibility to GONE
                    isAllFabsVisible = false;
                }
            }
        });

        // click this button will forward to add event interface
        addEventFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, AddEvent.class));
            }
        });

        viewReportFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bckgroundDimmer.setVisibility(View.INVISIBLE);
                addEventFloat.hide();
                viewReportFloat.hide();
                add_event_report.setVisibility(View.GONE);
                view_report_float.setVisibility(View.GONE);
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                AdminEventsReportFragment eventsFragment = new AdminEventsReportFragment();
                fragmentTransaction1.replace(R.id.content, eventsFragment);
                fragmentTransaction1.commit();
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
        AdminDiscoverFragment discoverFragment = new AdminDiscoverFragment();
        fragmentTransaction.replace(R.id.content, discoverFragment);
        fragmentTransaction.commit();
    }

    public static Bitmap decodeBase64(String base64String) {
        base64String = base64String.substring(base64String.indexOf(",") + 1);
        final byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void getAdminName(){
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

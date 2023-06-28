package com.aspinax.lanaevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            notLoggedIn();
        } else {
            Database db = new Database(new AsyncResponse() {
                @Override
                public void resultHandler(Map<String, Object> result, int resultCode) {
                    Person p = (Person) result.get(mAuth.getUid());
                    if (p == null) {
                        notLoggedIn();
                    } else {
                        Intent intent;
                        if (p.role.matches("Student")) intent = new Intent(MainActivity.this, StudentMainActivity.class);
                        else intent = new Intent(MainActivity.this, AdminMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void resultHandler(String msg, int resultCode) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            });

            db.read("users", mAuth.getUid(), Person.class, 0);
        }

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notLoggedIn() {
        Intent intent = new Intent(this, NoAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
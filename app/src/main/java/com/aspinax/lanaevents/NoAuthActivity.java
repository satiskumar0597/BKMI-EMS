package com.aspinax.lanaevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class NoAuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_auth);

        final Button sign_up = findViewById(R.id.sign_up);
        final Button login = findViewById(R.id.login);
        final RadioButton admin = findViewById(R.id.radioButton2);
        final RadioButton student = findViewById(R.id.radioButton3);
        final RadioGroup group = findViewById(R.id.radiogroup);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoAuthActivity.this, SignUpActivity.class);
                if(admin.isChecked()) {
                    intent.putExtra("role", "Admin");
                    startActivity(intent);
                }
                else if(student.isChecked()){
                    intent.putExtra("role", "Student");
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "Please select your Role", Toast.LENGTH_LONG).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(admin.isChecked() || student.isChecked())
                    startActivity(new Intent(NoAuthActivity.this, LoginActivity.class));
                else
                    Toast.makeText(getApplicationContext(), "Please select your Role", Toast.LENGTH_LONG).show();
            }
        });

        }

}
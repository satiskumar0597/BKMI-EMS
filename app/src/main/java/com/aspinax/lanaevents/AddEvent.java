package com.aspinax.lanaevents;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;


public class AddEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // One Preview Image
    ImageView IVPreviewImage;

    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;
    int day, month, year, hour, minute, second;
    int myday, myMonth, myYear, myHour, myMinute;
    TextInputEditText startDate, endDate, eventBannerPath;
    Timestamp start, end;
    String image = null, addedBy;
    Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final Button imageButton = findViewById(R.id.BSelectImage);
        final Button submitButton = findViewById(R.id.submit_btn);
        final Button cancelButton = findViewById(R.id.cancel);
        final Button dateButton = findViewById(R.id.btnPick);
        final Button dateButton2 = findViewById(R.id.btnPick2);
        final TextInputEditText eventName = findViewById(R.id.eventName);
        final TextView eventId = findViewById(R.id.eventID);
        eventId.setText("Event ID: "+genEventID());
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        final Spinner eventLocations = findViewById(R.id.event_locations);
        eventLocations.setOnItemSelectedListener(this);
        final Spinner eventType = findViewById(R.id.event_types);
        eventType.setOnItemSelectedListener(this);

        eventBannerPath = findViewById(R.id.eventBannerPath);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        getLoggedUser(mAuth.getUid());

        List<String> event_locations_list = new ArrayList<String>();
        event_locations_list.add("Select Location...");
        event_locations_list.add("Main Library");
        event_locations_list.add("Stadium Pusat Sukan");
        event_locations_list.add("Dewan Sultan Ibrahim");
        event_locations_list.add("Pusat Kokurikulum");
        event_locations_list.add("Arked UTHM");
        event_locations_list.add("FSKTM");
        event_locations_list.add("FKAAB");
        event_locations_list.add("Pusat Kebudaayaan Universiti");
        event_locations_list.add("Kor SISPA UTHM");

        List<String> event_type_list = new ArrayList<String>();
        event_type_list.add("Select Event Type");
        event_type_list.add("Meeting");
        event_type_list.add("Gathering");
        event_type_list.add("Competition");
        event_type_list.add("Training");
        event_type_list.add("Workshop");
        event_type_list.add("Exhibition");
        event_type_list.add("Trip");


        ArrayAdapter eventArray = new ArrayAdapter(this, android.R.layout.simple_spinner_item, event_locations_list);
        eventArray.setDropDownViewResource(android.R.layout.simple_spinner_item);
        eventLocations.setAdapter(eventArray);

        ArrayAdapter eventArray2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, event_type_list);
        eventArray2.setDropDownViewResource(android.R.layout.simple_spinner_item);
        eventType.setAdapter(eventArray2);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, AddEvent.this, year, month, day);
                datePickerDialog.show();
            }
        });

        dateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, AddEvent.this, year, month, day);
                datePickerDialog.show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eventname = Objects.requireNonNull(eventName.getText()).toString().trim();
                final String eventid = Objects.requireNonNull(eventId.getText()).toString().trim();
                final String startdate = Objects.requireNonNull(startDate.getText()).toString().trim();
                final String enddate = Objects.requireNonNull(endDate.getText()).toString().trim();
                final String eventlocation = String.valueOf(eventLocations.getSelectedItem());
                final String eventtype = String.valueOf(eventType.getSelectedItem());
                final String eventbannerpath = String.valueOf(eventBannerPath.getText());
                getLoggedUser(mAuth.getUid());
                final String orgId="BKMI";

                final int attendeeCount=0 , checkInCount = 0;
                final String type=eventtype;
                final boolean posted = false;
                final Map<String, Double> coordinates = new HashMap<>();


                if (TextUtils.isEmpty(eventname)) {
                    eventName.setError("Please enter the event name");
                    return;
                }
                if (TextUtils.isEmpty(eventid)) {
                    eventId.setError("Please enter the event id");
                    return;
                }
                if (TextUtils.isEmpty(startdate)) {
                    startDate.setError("Please enter the start date");
                    return;
                }
                if (TextUtils.isEmpty(enddate)) {
                    endDate.setError("Please enter the end date");
                    return;
                }
                if (TextUtils.isEmpty(eventlocation) || String.valueOf(eventLocations.getSelectedItem()).matches("Select Location...")) {
                    Toast.makeText(getApplicationContext(), "Make sure to select correct location ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(eventtype) || String.valueOf(eventType.getSelectedItem()).matches("Select Event Type...")) {
                    Toast.makeText(getApplicationContext(), "Make sure to select event type ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(eventbannerpath)) {
                    eventBannerPath.setError("Please choose a image");
                    return;
                }

                if(String.valueOf(eventLocations.getSelectedItem()).matches("Main Library")) {
                    coordinates.put("latitude", 1.8574463);
                    coordinates.put("longitude", 103.0797796);
                }
                else if (String.valueOf(eventLocations.getSelectedItem()).matches("Stadium Pusat Sukan")){
                    coordinates.put("latitude" , 2.4894334);
                    coordinates.put("longitude" , 101.1241743);
                }
                else if (String.valueOf(eventLocations.getSelectedItem()).matches("Dewan Sultan Ibrahim")){
                    coordinates.put("latitude" ,1.8572606);
                    coordinates.put("longitude" ,103.0798912);
                }
                else if (String.valueOf(eventLocations.getSelectedItem()).matches("Pusat Kokurikulum")){
                    coordinates.put("latitude" , 1.8572606);
                    coordinates.put("longitude" , 103.0798912);
                }
                else if (String.valueOf(eventLocations.getSelectedItem()).matches("Arked UTHM")){
                    coordinates.put("latitude" , 1.8540115);
                    coordinates.put("longitude" , 103.0800521);
                }
                else if(String.valueOf(eventLocations.getSelectedItem()).matches("FSKTM")){
                    coordinates.put("latitude" , 1.8597698);
                    coordinates.put("longitude" , 103.0825949);
                }
                else if(String.valueOf(eventLocations.getSelectedItem()).matches("FKAAB")){
                    coordinates.put("latitude" , 1.8600808);
                    coordinates.put("longitude" , 103.0829704);
                }
                else if(String.valueOf(eventLocations.getSelectedItem()).matches("Pusat Kebudaayaan Universiti")){
                    coordinates.put("latitude" , 1.8539364);
                    coordinates.put("longitude" ,103.0817795);
                }
                else if(String.valueOf(eventLocations.getSelectedItem()).matches("Kor SISPA UTHM")){
                    coordinates.put("latitude" , 1.8517703);
                    coordinates.put("longitude" , 103.0793548);
                }
                else Toast.makeText(eventArray.getContext(),("Please select the location..."), Toast.LENGTH_LONG).show();

                image = BitMapToString(imageBitmap);

                Event.saveEvent(mAuth, addedBy, attendeeCount, checkInCount, end, eventlocation, eventname, eventid, orgId, posted, start, type, coordinates, image, imageBitmap,mAuth.getCurrentUser().getUid());
                Intent intent = new Intent(AddEvent.this, AdminMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddEvent.this, AdminMainActivity.class));
            }
        });
    }

    void getLoggedUser(String docId){
        String TAG="";
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        DocumentReference docRef = db1.collection("users").document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        addedBy=document.getString("fName");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

        // this function is triggered when
        // the Select Image Button is clicked
        void imageChooser() {

            // create an instance of the
            // intent of the type image
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);

            // pass the constant to compare it
            // with the returned requestCode
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        }

        // this function is triggered when user
        // selects the image from the imageChooser
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {

                // compare the resultCode with the
                // SELECT_PICTURE constant
                if (requestCode == SELECT_PICTURE) {
                    // Get the url of the image from data
                    Uri selectedImageUri = data.getData();
                    eventBannerPath.setText("/sdCard/Downloads");
                    if (null != selectedImageUri) {
                        // update the preview image in the layout
                        IVPreviewImage.setImageURI(selectedImageUri);

                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this, AddEvent.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        Date currentDate = new Date(myYear-1900,myMonth,myday,myHour,myMinute);
        System.out.println("current Date:----------------------------------------- " + currentDate);
        System.out.println("current Date:----------------------------------------- " + currentDate.getTime());

        if (startDate.getText().toString().isEmpty()){
            startDate.setText( "From "+myday+"/"+(myMonth+1)+"/"+myYear +" at "+ myHour+":"+ myMinute);
            start = new Timestamp(currentDate);
        }
        else{
            endDate.setText("Until "+myday+"/"+(myMonth+1)+"/"+myYear +" at "+ myHour+":"+ myMinute);
            end = new Timestamp(currentDate);
        }

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String genEventID(){
        String key = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        StringBuilder genID = new StringBuilder(5);

        for (int i=0; i<5; i++){
            int index = (int)(key.length()* Math.random());
            genID.append(key.charAt(index));
        }
        return genID.toString();
    }
}

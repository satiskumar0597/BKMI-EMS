package com.aspinax.lanaevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mapbox.mapboxsdk.Mapbox;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class AdminEventReportDetails extends AppCompatActivity {
    private Database db;
    String[] totalStudents;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoic3QzdjNubXciLCJhIjoiY2s3bHh5OG42MGM1aDNrcDZyNXlkZXB2NCJ9.QjrMAZJvETZJAQHC8-0tsw");
        setContentView(R.layout.activity_report_details);
        Intent intent     = getIntent();
        String eventId    = intent.getStringExtra("eventId");
        String name       = intent.getStringExtra("name");
        int attendeeCount = intent.getIntExtra("attendeeCount", 0);
        long end          = intent.getLongExtra("end", 0);
        String location   = intent.getStringExtra("location");
        String orgId      = intent.getStringExtra("orgId");
        long start        = intent.getLongExtra("start", 0);
        String type       = intent.getStringExtra("type");
        String image      = intent.getStringExtra("image");
        int checkInCount  = intent.getIntExtra("checkInCount", 0);
        String addedBy    = intent.getStringExtra("addedBy");
        String userId     = intent.getStringExtra("userId");

        Map<String , Double> coordinates = new HashMap<>();
        coordinates.put("longitude", intent.getDoubleExtra("longitude", 0));
        coordinates.put("latitude", intent.getDoubleExtra("latitude", 0));

        final Event event = new Event(addedBy, attendeeCount, checkInCount, new Timestamp(end, 0), image, location, name, eventId, orgId, true,  new Timestamp(start, 0), type, coordinates, userId);
        event.setEventId(eventId);


        TextView eventNameView = findViewById(R.id.eventname);
        eventNameView.setText(event.name);

        TextView fromView = findViewById(R.id.eventdate);
        SimpleDateFormat startDate = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        fromView.setText(startDate.format(event.start.toDate()));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");

        final TextView countView = findViewById(R.id.checkincount);
        if (event.end.getSeconds() < getTodayDate().getTime()/1000L) countView.setText(event.checkInCount + " went");
        countView.setText(event.checkInCount + " going");

        TextView evnttime = findViewById(R.id.eventtime);
        TextView evntlocation = findViewById(R.id.eventlocation);
        TextView evntcreator = findViewById(R.id.createdby);
        TextView evntid = findViewById(R.id.eventid);
        TextView evnttype = findViewById(R.id.eventtype);

        evnttime.setText(sdf.format(event.start.toDate()));
        evntlocation.setText(event.location);
        evntcreator.setText("Created by: "+event.addedBy);
        evntid.setText(event.eventId);
        evnttype.setText(event.type);

        //String[] mobileArray =
                getAttendanceReport(eventId);
        //System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"+mobileArray);

        //get array from attendance
       // ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.custom_listview_rows, mobileArray);
       // ListView attendeeList = findViewById(R.id.attenceeLists);
        //attendeeList.setAdapter(adapter);


        // Back button to go to home page
        ImageView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

//        db = new Database(new AsyncResponse() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void resultHandler(Map<String, Object> result, int resultCode) {
//                if (resultCode == 0) {
//                    if (result.isEmpty()) {
////                        book.setEnabled(true);
////                        book.setOnClickListener(v -> {
////                            if (event.end.getSeconds() < getTodayDate().getTime()/1000L) {
////                                Toast.makeText(getApplicationContext(), "The deadline to book has passed", Toast.LENGTH_LONG).show();
////                            } else {
////                                Map<String, Object> data = new HashMap<>();
////                                data.put("eventId", event.eventId);
////                                data.put("userId", mAuth.getUid());
////                                data.put("createdAt", FieldValue.serverTimestamp());
////                                data.put("end", event.end);
////                                book.setOnClickListener(null);
////                                db.add("tickets", data, 1);
////                            }
////                        });
//                    } else {
////                       // markAsBooked(book);
////                        String ticketId = (String) result.keySet().toArray()[0];
////                        Ticket t = (Ticket) result.get(ticketId);
////                        assert t != null;
////                        book.setEnabled(true);
////                        t.setTicketId(ticketId);
////                        TextView ticketIdView = findViewById(R.id.ticketId);
////                        ticketIdView.setText("# " + t.ticketId);
////                        TextView hereView = findViewById(R.id.here_your);
////                        hereView.setVisibility(View.VISIBLE);
////                        LinearLayout ticketGroupView = findViewById(R.id.ticket_group);
////                        ticketGroupView.setVisibility(View.VISIBLE);
////                        ImageView qrCodeView = findViewById(R.id.ticketQR);
////                        try {
////                            Bitmap qrBitMap = encodeAsBitmap(t.ticketId);
////                            if(qrBitMap != null) qrCodeView.setImageBitmap(qrBitMap);
////                        } catch (WriterException ignored) { }
//
//
//                    }
//                }
//            }
//
//            @Override
//            public void resultHandler(String msg, int resultCode) {
//                if (resultCode == 1) {
//                    //  markAsBooked(book);
//                    db.increment("events", event.eventId, "attendeeCount", 1, 2);
//                    db.filterWithTwoFields("tickets", "userId", mAuth.getUid(), "eventId", event.eventId, Ticket.class, 0);
//                } else if (resultCode == 2){
//                    event.attendeeCount += 1;
//                    countView.setText(event.attendeeCount + " going");
//                } else {
//                    Toast.makeText(AdminEventReportDetails.this, msg, Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        //db.filterWithTwoFields("tickets", "userId", mAuth.getUid(), "eventId", event.eventId, Ticket.class, 0);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

     void getAttendanceReport(String docId){
        String TAG="";
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        DocumentReference docRef = db1.collection("attendance").document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> group = (List<String>) document.get("attendance");
                        totalStudents = new String[group.size()];

                        for (int i = 0; i < group.size(); i++){
                            totalStudents[i] = (i+1)+". "+group.get(i);
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                         ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_listview_rows, totalStudents);
                         ListView attendeeList = findViewById(R.id.attendeeLists);
                         attendeeList.setAdapter(adapter);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void markAsBooked(MaterialButton book) {
        book.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        book.setText("Booked");
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 200, 200, null);
        } catch (IllegalArgumentException e) {
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, w, h);
        return bitmap;
    }

    static Date getTodayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}




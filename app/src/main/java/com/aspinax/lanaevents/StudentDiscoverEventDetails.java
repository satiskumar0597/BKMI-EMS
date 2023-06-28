package com.aspinax.lanaevents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class StudentDiscoverEventDetails extends AppCompatActivity {
    private Database db;
    private MapView mapView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoic3QzdjNubXciLCJhIjoiY2s3bHh5OG42MGM1aDNrcDZyNXlkZXB2NCJ9.QjrMAZJvETZJAQHC8-0tsw");
        setContentView(R.layout.activity_student_discover_event_details);

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

       // qrCode = findViewById(R.id.qrcode);

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

       // genQRcode(event.eventId, event.end.toDate() );

        // Back button to go to home page
        ImageView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(event.coordinates.get("latitude"), event.coordinates.get("longitude")))
                        .zoom(16)
                        .build());

                mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(event.coordinates.get("latitude"), event.coordinates.get("longitude")))
                                .icon(IconFactory.getInstance(StudentDiscoverEventDetails.this).fromResource(R.drawable.ic_map_pin))
                        );
                    }
                });
            }
        });

        final MaterialButton book = findViewById(R.id.registerButton);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        db = new Database(new AsyncResponse() {
            @SuppressLint("SetTextI18n")
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                if (resultCode == 0) {
                    if (result.isEmpty()) {
                        book.setEnabled(true);
                        book.setOnClickListener(v -> {
                            if (event.end.getSeconds() < getTodayDate().getTime()/1000L) {
                                Toast.makeText(getApplicationContext(), "The deadline to book has passed", Toast.LENGTH_LONG).show();
                            } else {
                                Map<String, Object> data = new HashMap<>();
                                data.put("eventId", event.eventId);
                                data.put("userId", mAuth.getUid());
                                data.put("createdAt", FieldValue.serverTimestamp());
                                data.put("end", event.end);
                                data.put("start", event.start);
                                data.put("location",event.location );
                                data.put("name", event.name);
                                data.put("type", event.type);
                                data.put("checkInCount", event.checkInCount);
                                data.put("addedBy", event.addedBy );
                                data.put("image", event.image);

                                Map<String , Double> coordinates = new HashMap<>();
                                coordinates.put("longitude", intent.getDoubleExtra("longitude", 0));
                                coordinates.put("latitude", intent.getDoubleExtra("latitude", 0));
                                data.put("coordinates",coordinates);

                                book.setOnClickListener(null);
                                db.set("registered", eventId, data, 1);
                                updateRegisteredStud(checkInCount,eventId);
                            }
                        });
                    } else {
                        markAsBooked(book);
                        String ticketId = (String) result.keySet().toArray()[0];
                        Ticket t = (Ticket) result.get(ticketId);
                        assert t != null;
                        book.setEnabled(true);
                        t.setTicketId(ticketId);
                        //TextView ticketIdView = findViewById(R.id.ticketId);
                        //ticketIdView.setText("# " + t.ticketId);
                        //TextView hereView = findViewById(R.id.here_your);
                        //hereView.setVisibility(View.VISIBLE);
                        //LinearLayout ticketGroupView = findViewById(R.id.ticket_group);
                        //ticketGroupView.setVisibility(View.VISIBLE);
                        //ImageView qrCodeView = findViewById(R.id.ticketQR);
//                        try {
//                            Bitmap qrBitMap = encodeAsBitmap(t.ticketId);
//                            if(qrBitMap != null) qrCodeView.setImageBitmap(qrBitMap);
//                        } catch (WriterException ignored) { }
                    }
                }
            }

            @Override
            public void resultHandler(String msg, int resultCode) {
                if (resultCode == 1) {
                    markAsBooked(book);
                    db.increment("events", event.eventId, "attendeeCount", 1, 2);
                    db.filterWithTwoFields("registered", "userId", mAuth.getUid(), "eventId", event.eventId, Ticket.class, 0);
                } else if (resultCode == 2){
                    event.attendeeCount += 1;
                    countView.setText(event.attendeeCount + " going");
                } else {
                    Toast.makeText(StudentDiscoverEventDetails.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        db.filterWithTwoFields("registered", "userId", mAuth.getUid(), "eventId", event.eventId, Ticket.class, 0);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    void updateRegisteredStud(int currr, String docId){
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        db1.collection("events").document(docId).update(
                "checkInCount",currr+1);

    }

    private void markAsBooked(MaterialButton book) {
        book.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        book.setText("Registered");
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

//    public void genQRcode(String eventDocId, Date eventdate) {
//        if (eventdate.compareTo(getTodayDate())>0){
//            //initializing MultiFormatWriter for QR code
//            MultiFormatWriter mWriter = new MultiFormatWriter();
//            try {
//                //BitMatrix class to encode entered text and set Width & Height
//                BitMatrix mMatrix = mWriter.encode(eventDocId, BarcodeFormat.QR_CODE, 400, 400);
//                BarcodeEncoder mEncoder = new BarcodeEncoder();
//                Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
//                qrCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView
//                notificationforQR.setText("Scan for Attendance");
//            } catch (WriterException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//            notificationforQR.setText("QRCode for attendance not available. Event is ended");
//
//    }

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
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }
}





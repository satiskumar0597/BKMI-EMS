package com.aspinax.lanaevents;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDiscoverFragment extends Fragment {


    public AdminDiscoverFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_admin_discover, container, false);
        Database db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                List<Event> currrentEventList = new ArrayList<>();
                List<Event> pastEventList = new ArrayList<>();
                for (String eventId : result.keySet()) {
                    Event event = (Event) result.get(eventId);
                    assert event != null;
                    event.setEventId(eventId);
                    if (event.end.getSeconds() >= getTodayDate().getTime()/1000L) {
                        currrentEventList.add(event);
                    } else pastEventList.add(event);
                }
                if (resultCode == 0) {
                    Collections.sort(currrentEventList);
                    Collections.sort(pastEventList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    RecyclerView eventsListView = view.findViewById(R.id.eventsList);
                    eventsListView.setLayoutManager(layoutManager);
                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    RecyclerView pastEventsListView = view.findViewById(R.id.pastEventsList);
                    pastEventsListView.setLayoutManager(layoutManager2);
                    if (getContext() != null) {
                        AdminEventsAdapter currentAdapter = new AdminEventsAdapter(getContext(), currrentEventList);
                        AdminEventsAdapter pastAdapter = new AdminEventsAdapter(getContext(), pastEventList);
                        eventsListView.setAdapter(currentAdapter);
                        pastEventsListView.setAdapter(pastAdapter);
                    }
                }
            }

            @Override
            public void resultHandler(String msg, int resultCode) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        db.readCollection("events", Event.class, 0);
        return view;
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



}
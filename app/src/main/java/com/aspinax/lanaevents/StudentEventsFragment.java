package com.aspinax.lanaevents;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.aspinax.lanaevents.AdminDiscoverFragment.getTodayDate;

public class StudentEventsFragment extends Fragment {
    private static final String TAG = "";
    private Database db;
    String name;
    public StudentEventsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_student_events, container, false);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) {
                List<Event> eventList = new ArrayList<>();
                for (String eventId: result.keySet()) {
                    Event event = (Event) result.get(eventId);
                    assert event != null;
                    event.setEventId(eventId);
                    eventList.add(event);
                }

                if (resultCode == 0) {
                    Collections.sort(eventList);
                    StudentEventsAdapter eventsAdapter = new StudentEventsAdapter(getContext(), eventList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    RecyclerView eventsListView = view.findViewById(R.id.eventsList);
                    eventsListView.setLayoutManager(layoutManager);
                    if (getContext() != null) {
                        eventsListView.setAdapter(eventsAdapter);
                    }
                    db.filterWithOneFieldAndCompareLess("registered", "userId", mAuth.getCurrentUser().getUid(), "end", getTodayDate(), Event.class, 1);
                } else {
                    Collections.sort(eventList, Collections.<Event>reverseOrder());
                    StudentEventsAdapter eventsAdapter = new StudentEventsAdapter(getContext(), eventList);
                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    RecyclerView  pastEventsListView = view.findViewById(R.id.pastEventsList);
                    pastEventsListView.setLayoutManager(layoutManager2);
                    if (getContext() != null) {
                        pastEventsListView.setAdapter(eventsAdapter);
                    }
                }
            }

            @Override
            public void resultHandler(String msg, int resultCode) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
        //this is for upcoming
        db.filterWithOneFieldAndCompareGreater("registered", "userId", mAuth.getCurrentUser().getUid(), "end", getTodayDate(), Event.class,0);
        return view;
    }
}

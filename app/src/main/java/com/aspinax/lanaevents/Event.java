package com.aspinax.lanaevents;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

import static com.aspinax.lanaevents.AdminMainActivity.decodeBase64;

public class Event implements Comparable<Event> {
    public String addedBy, eventId, location, name, orgId, image, userId, type;
    public Map<String, Double> coordinates;
    public int attendeeCount, checkInCount;
    public Timestamp start, end;
    public boolean posted=true;
    public Bitmap imageBitmap;

    public Event() {}
    Event(String addedBy, int attendeeCount, int checkInCount, Timestamp end, String image, String location, String name, String eventid, String orgId, boolean posted, Timestamp start, String type, Map<String, Double> coordinates, String userId) {
        this.addedBy = addedBy;
        this.attendeeCount = attendeeCount;
        this.checkInCount = checkInCount;
        this.end = end;
        this.location = location;
        this.name = name;
        this.eventId = eventid;
        this.orgId = orgId;
        this.posted = posted;
        this.start = start;
        this.type = type;
        this.coordinates = coordinates;
        this.image = image;
        this.imageBitmap = Bitmap.createScaledBitmap(decodeBase64(image), 720, 430, true);
        this.userId = userId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public void setAttendeeCount(int attendeeCount) {
        this.attendeeCount = attendeeCount;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    public void setCoordinates(Map<String, Double> coordinates) {
        this.coordinates = coordinates;
    }

    public void setImage(String image) {
        this.image = image;
        this.imageBitmap = decodeBase64(image);
    }
    public void setUserId(String userId){this.userId = userId;}

    @Override
    public int compareTo(Event o) {
        return Long.compare(this.end.getSeconds(), o.end.getSeconds());
    }

    static void saveEvent(FirebaseAuth auth, String addedBy, int attendeeCount, int checkInCount, Timestamp  end, String location, String name, String eventid, String orgId, boolean posted, Timestamp start, String type, Map<String, Double> coordinates, String image, Bitmap imageBitmap, String userId) {
        FirebaseUser user = auth.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("addedBy", addedBy);
        data.put("attendeeCount", attendeeCount);   // after create
        data.put("checkInCount", checkInCount);     // after create
        data.put("end", end);
        data.put("location", location);
        data.put("name", name);
        data.put("eventId", eventid);
        data.put("orgId", orgId);
        data.put("posted", posted);
        data.put("start", start);
        data.put("type", type);
        data.put("coordinates", coordinates);
        data.put("image", image);
        //data.put("imageBitmap", Bitmap.createScaledBitmap(decodeBase64(image), 720, 430, true));
        data.put("userId", userId);

        Database db = new Database(new AsyncResponse() {
            @Override
            public void resultHandler(Map<String, Object> result, int resultCode) { }

            @Override
            public void resultHandler(String msg, int resultCode) {

            }
        });
        assert user != null;
        db.set("events", eventid, data, 0);

        Map<String, Object> test = new HashMap<>();
        test.put("attendance", "");
        db.set("attendance", eventid, test,0);
    }

}
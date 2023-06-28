package com.aspinax.lanaevents;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {
    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore db;
    private AsyncResponse response;

    private static final String TAG = "Database";

    public Database(AsyncResponse obj) {
        db = FirebaseFirestore.getInstance();
        this.response = obj;
    }

    // CREATE with predefined document id
    void set(String collectionName, String docId, Map data, final int resultCode) {
        db.collection(collectionName).document(docId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.resultHandler("Success", resultCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), resultCode);
                    }
                });
    }

    void setDoc(String collectionName, String docId, Map data, final int resultCode) {
        db.collection(collectionName).document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.resultHandler("Success", resultCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), resultCode);
                    }
                });
    }

    // CREATE with auto id
    private void addC(CollectionReference collection, Map data, final int resultCode) {
        collection
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        response.resultHandler("Success", resultCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), resultCode);
                    }
                });
    }

    void add(String collectionName, Map data, int resultCode) {
        addC(db.collection(collectionName), data, resultCode);
    }

    void addToSubCollection(String collectionName, String docId, String subName, Map data, int resultCode) {
        CollectionReference collection = db.collection(collectionName)
                .document(docId)
                .collection(subName);
        addC(collection, data, resultCode);
    }

    // QUERY
    private void query(Query q, final Class<?> className, final int resultCode) {
        final Map<String, Object> result = new HashMap<>();
        q.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {
                                result.put(document.getId(), document.toObject(className));
                            }
                            response.resultHandler(result, resultCode);
                        } else {
                            response.resultHandler(Objects.requireNonNull(task.getException()).toString(), 0);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), 0);
                    }
                });
    }

    // READ ENTIRE COLLECTION
    void readCollection(String collectionName, final Class<?> className, final int resultCode) {
        final Map<String, Object> result = new HashMap<>();
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {
                                result.put(document.getId(), document.toObject(className));
                            }
                            response.resultHandler(result, resultCode);
                        } else {
                            response.resultHandler(Objects.requireNonNull(task.getException()).toString(), 0);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), 0);
                    }
                });
    }

    void getLoginDocument(String email, final Class<?> className, final int resultCode){
        final Map<String, Object> result = new HashMap<>();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println("----------------"+document.getId());
                                System.out.println("-------------------"+document.getData());

                                result.put("fName", document.get("fName"));
                                result.put("lName", document.get("lName"));
                                result.put("email", document.get("email"));
                                result.put("mNumber", document.get("mNumber"));
                                result.put("role", document.get("role"));
                                result.put("organization", document.get("organization"));
                                result.put("documentID", document.getId());
                                result.put("phoneNumber", document.get("phoneNumber"));
                            }
                            response.resultHandler(result, resultCode);
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //list admin specific created events
    String getAdminName(String usersid, final Class<?> className, final int resultCode){
        final Map<String, Object> result = new HashMap<>();

        FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
        DocumentReference docRef = dbInstance.collection("users").document(usersid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //name = document.getString("fName");
                        result.put("addedBy",document.get("fName"));
                        System.out.println(document.get("fName")+" YYY flflflflfflflllflfflflflflflfflflflflfl");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        }); System.out.println(result.get("addedBy")+" ZZZ flflflflfflflllflfflflflflflfflflflflfl");
        return (String) result.get("addedBy");
    }

    // READ SINGLE DOCUMENT
    void read(String collectionName, String docId, final Class<?> className, final int resultCode) {
        final Map<String, Object> result = new HashMap<>();
        db.collection(collectionName).document(docId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                result.put(document.getId(), document.toObject(className));
                            }
                            if (response != null) {
                                response.resultHandler(result, resultCode);
                            }
                        } else {
                            response.resultHandler(Objects.requireNonNull(task.getException()).toString(), 0);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), 0);
                    }
                });
    }

    // FILTER, 1
    void filterWithOneField(String collectionName, String field, Object filter, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereEqualTo(field, filter);
        query(q, className, resultCode);
    }

    // FILTER, 2
    void filterWithTwoFields(String collectionName, String field1, Object filter1, String field2, Object filter2, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereEqualTo(field1, filter1)
                .whereEqualTo(field2, filter2);
        query(q, className, resultCode);
    }

    // FILTER, 3
    void filterWithOneFieldAndCompareGreater(String collectionName, String field1, Object filter1, String field2, Object filter2, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereEqualTo(field1, filter1)
                .whereGreaterThan(field2, filter2);
        query(q, className, resultCode);
    }

    // FILTER, 4
    void filterWithOneFieldAndCompareLess(String collectionName, String field1, Object filter1, String field2, Object filter2, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereEqualTo(field1, filter1)
                .whereLessThan(field2, filter2);
        query(q, className, resultCode);
    }

    // FILTER, 5
    void compareLess(String collectionName, String field, Object filter, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereLessThan(field, filter);
        query(q, className, resultCode);
    }

    // FILTER, 6
    void compareGreater(String collectionName, String field, Object filter, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereGreaterThan(field, filter);
        query(q, className, resultCode);
    }

    // FILTER, 7
    void compareLessOrderBy(String collectionName, String field, Object filter, String orderField, Query.Direction dir, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereLessThan(field, filter)
                .orderBy(orderField, dir);
        query(q, className, resultCode);
    }

    // FILTER, 8
    void compareGreaterThanOrderBy(String collectionName, String field, Object filter, String orderField, Query.Direction dir, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereGreaterThan(field, filter)
                .orderBy(orderField, dir);
        query(q, className, resultCode);
    }

    // FILTER, 3
    void filterWithOneFieldAndCompareGreaterOrder(String collectionName, String field1, Object filter1, String field2, Object filter2, String orderField, Query.Direction dir, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereEqualTo(field1, filter1)
                .whereGreaterThan(field2, filter2)
                .orderBy(orderField, dir);
        query(q, className, resultCode);
    }

    // FILTER, 4
    void filterWithOneFieldAndCompareLessOrder(String collectionName, String field1, Object filter1, String field2, Object filter2,  String orderField, Query.Direction dir, Class<?> className, int resultCode) {
        Query q = db.collection(collectionName)
                .whereEqualTo(field1, filter1)
                .whereLessThan(field2, filter2)
                .orderBy(orderField, dir);
        query(q, className, resultCode);
    }

    // FILTER, 5 // Firestore range limitation
//    void filterWithTwoFieldsWithinRangeOrder(String collectionName, String field1, Object filter1,
//                                            String field2, Object filter2, String orderField, Query.Direction dir, Class<?> className, int resultCode) {
//        Query q = db.collection(collectionName)
//                .whereGreaterThan(field1, filter1)
//                .whereLessThan(field2, filter2)
//                .orderBy(orderField, dir);
//        query(q, className, resultCode);
//    }

    // UPDATE
    void update(String collectionName, String docId, Map<String, Object> data, final int resultCode) {
        db.collection(collectionName).document(docId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.resultHandler("Success", resultCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), resultCode);
                    }
                });
    }

    // INCREMENT, DECREMENT
    void increment(String collectionName, String docId, String fieldName, Integer val, int resultCode) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldName, FieldValue.increment(val));
        this.update(collectionName, docId, updates, resultCode);
    }

    // DELETE
    void delete(String collectionName, String docId) {
        db.collection(collectionName).document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.resultHandler(e.toString(), 0);
                    }
                });
    }

    // DELETE FIELDS
    void deleteFields(String collectionName, String docId, String[] fields, int resultCode) {
        Map<String, Object> updates = new HashMap<>();
        for (String field: fields) {
            updates.put(field, FieldValue.delete());
        }
        this.update(collectionName, docId, updates, resultCode);
    }
}
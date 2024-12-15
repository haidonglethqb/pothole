package com.example.pothole.MapScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import kotlin.Triple;

public class LocationRetriever {
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String LOCATIONS_KEY = "locations";
    private DatabaseReference databaseReference;
    private List<Triple<Double, Double, String>> locationList;
    private SharedPreferences sharedPreferences;

    public LocationRetriever(Context context) {
        databaseReference = FirebaseDatabase.getInstance().getReference("potholes");
        locationList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void retrieveLocations(final LocationCallback callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    String severity = snapshot.child("severity").getValue(String.class); // Retrieve severity
                    if (latitude != null && longitude != null && severity != null) {
                        locationList.add(new Triple<>(latitude, longitude, severity));
                    }
                }
                saveLocationsToLocalStorage(locationList);
                callback.onLocationsRetrieved(locationList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    private void saveLocationsToLocalStorage(List<Triple<Double, Double, String>> locations) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (Triple<Double, Double, String> location : locations) {
            sb.append(location.getFirst()).append(",").append(location.getSecond()).append(",").append(location.getThird()).append(";");
        }
        editor.putString(LOCATIONS_KEY, sb.toString());
        editor.apply();
    }

    public List<Triple<Double, Double, String>> getLocationsFromLocalStorage() {
        List<Triple<Double, Double, String>> locations = new ArrayList<>();
        String savedLocations = sharedPreferences.getString(LOCATIONS_KEY, "");
        if (!savedLocations.isEmpty()) {
            String[] locationPairs = savedLocations.split(";");
            for (String pair : locationPairs) {
                String[] latLng = pair.split(",");
                if (latLng.length == 3) {
                    Double latitude = Double.parseDouble(latLng[0]);
                    Double longitude = Double.parseDouble(latLng[1]);
                    String severity = latLng[2];
                    locations.add(new Triple<>(latitude, longitude, severity));
                }
            }
        }
        return locations;
    }

    public void logStoredLocations() {
        List<Triple<Double, Double, String>> locations = getLocationsFromLocalStorage();
        for (Triple<Double, Double, String> location : locations) {
            Log.d("LocationRetriever", "Latitude: " + location.getFirst() + ", Longitude: " + location.getSecond() + ", Severity: " + location.getThird());
        }
    }

    public interface LocationCallback {
        void onLocationsRetrieved(List<Triple<Double, Double, String>> locations);
        void onError(Exception e);
    }
}
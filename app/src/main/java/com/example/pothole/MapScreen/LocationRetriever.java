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



public class LocationRetriever {
    public class Quadruple<F, S, T, Q> {
        private final F first;
        private final S second;
        private final T third;
        private final Q fourth;

        public Quadruple(F first, S second, T third, Q fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }

        public T getThird() {
            return third;
        }

        public Q getFourth() {
            return fourth;
        }
    }
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String LOCATIONS_KEY = "locations";
    private DatabaseReference databaseReference;
    private List<Quadruple<Double, Double, String, String>> locationList;
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
                    String severity = snapshot.child("severity").getValue(String.class);
                    String dateTime = snapshot.child("dateTime").getValue(String.class); // Retrieve dateTime
                    if (latitude != null && longitude != null && severity != null && dateTime != null) {
                        locationList.add(new Quadruple<>(latitude, longitude, severity, dateTime));
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

    private void saveLocationsToLocalStorage(List<Quadruple<Double, Double, String, String>> locations) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (Quadruple<Double, Double, String, String> location : locations) {
            sb.append(location.getFirst()).append(",").append(location.getSecond()).append(",").append(location.getThird()).append(",").append(location.getFourth()).append(";");
        }
        editor.putString(LOCATIONS_KEY, sb.toString());
        editor.apply();
    }

    public List<Quadruple<Double, Double, String, String>> getLocationsFromLocalStorage() {
        List<Quadruple<Double, Double, String, String>> locations = new ArrayList<>();
        String savedLocations = sharedPreferences.getString(LOCATIONS_KEY, "");
        if (!savedLocations.isEmpty()) {
            String[] locationPairs = savedLocations.split(";");
            for (String pair : locationPairs) {
                String[] latLng = pair.split(",");
                if (latLng.length == 4) {
                    Double latitude = Double.parseDouble(latLng[0]);
                    Double longitude = Double.parseDouble(latLng[1]);
                    String severity = latLng[2];
                    String dateTime = latLng[3];
                    locations.add(new Quadruple<>(latitude, longitude, severity, dateTime));
                }
            }
        }
        return locations;
    }

    public void logStoredLocations() {
        List<Quadruple<Double, Double, String, String>> locations = getLocationsFromLocalStorage();
        for (Quadruple<Double, Double, String, String> location : locations) {
            Log.d("LocationRetriever", "Latitude: " + location.getFirst() + ", Longitude: " + location.getSecond() + ", Severity: " + location.getThird() + ", DateTime: " + location.getFourth());
        }
    }

    public interface LocationCallback {
        void onLocationsRetrieved(List<Quadruple<Double, Double, String, String>> locations);
        void onError(Exception e);
    }
}
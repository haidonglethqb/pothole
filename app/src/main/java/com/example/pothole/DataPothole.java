package com.example.pothole;


import android.widget.TextView;

public class DataPothole {

    public String severity;

    public float deltaX, deltaY, deltaZ, combinedDelta;
    public double latitude, longitude;
    public long timestamp;
    private String dateTime;

    public DataPothole() {
        // Default constructor required for calls to DataSnapshot.getValue(SensorData.class)
    }

    public DataPothole(String severity, float deltaX, float deltaY, float deltaZ,
                       float combinedDelta, double latitude, double longitude,
                       long timestamp, String dateTime) {

        this.severity = severity;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
        this.combinedDelta = combinedDelta;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.dateTime = dateTime;

    }

    public DataPothole(String severity, float deltaX, float deltaY, float deltaZ, TextView combinedDelta, double latitude, double longitude, long timestamp, String dateTime) {

    }


    @Override
    public String toString() {
        return "Pothole Data{" +
                "currentX=" + deltaX +
                ", currentY=" + deltaY +
                ", currentZ=" + deltaZ +
                ", combinedDelta=" + combinedDelta +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}

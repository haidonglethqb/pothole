package com.example.pothole;

public class PotholeData {
    private int potholeCount;
    private String severity;
    private float deltaX;
    private float deltaY;
    private float deltaZ;
    private float combinedDelta;
    private double latitude;
    private double longitude;
    private long timestamp;
    private String dateTime;

    // No-argument constructor required for Firebase
    public PotholeData() {}

    public PotholeData(int potholeCount, String severity, float deltaX, float deltaY, float deltaZ,
                       float combinedDelta, double latitude, double longitude,
                       long timestamp, String dateTime) {
        this.potholeCount = potholeCount;
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

    // Getters
    public int getPotholeCount() {
        return potholeCount;
    }

    public String getSeverity() {
        return severity;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public float getDeltaZ() {
        return deltaZ;
    }

    public float getCombinedDelta() {
        return combinedDelta;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    // Setters
    public void setPotholeCount(int potholeCount) {
        this.potholeCount = potholeCount;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setDeltaX(float deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(float deltaY) {
        this.deltaY = deltaY;
    }

    public void setDeltaZ(float deltaZ) {
        this.deltaZ = deltaZ;
    }

    public void setCombinedDelta(float combinedDelta) {
        this.combinedDelta = combinedDelta;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "PotholeData{" +
                "potholeCount=" + potholeCount +
                ", severity='" + severity + '\'' +
                ", deltaX=" + deltaX +
                ", deltaY=" + deltaY +
                ", deltaZ=" + deltaZ +
                ", combinedDelta=" + combinedDelta +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
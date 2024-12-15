package com.example.pothole.Other;

public class HistoryItem {
    private double combinedDelta;
    private String dateTime;
    private double deltax;
    private double deltay;
    private double deltaz;
    private double latitude;
    private double longitude;
    private int potholeCount;
    private String severity;
    private long timestamp;

    // Constructor mặc định cho Firebase
    public HistoryItem() {
    }

    public HistoryItem(double combinedDelta, String dateTime, double deltax, double deltay, double deltaz,
                       double latitude, double longitude, int potholeCount, String severity, long timestamp) {
        this.combinedDelta = combinedDelta;
        this.dateTime = dateTime;
        this.deltax = deltax;
        this.deltay = deltay;
        this.deltaz = deltaz;
        this.latitude = latitude;
        this.longitude = longitude;
        this.potholeCount = potholeCount;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    // Getter và Setter
    public double getCombinedDelta() {
        return combinedDelta;
    }

    public void setCombinedDelta(double combinedDelta) {
        this.combinedDelta = combinedDelta;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getDeltax() {
        return deltax;
    }

    public void setDeltax(double deltax) {
        this.deltax = deltax;
    }

    public double getDeltay() {
        return deltay;
    }

    public void setDeltay(double deltay) {
        this.deltay = deltay;
    }

    public double getDeltaz() {
        return deltaz;
    }

    public void setDeltaz(double deltaz) {
        this.deltaz = deltaz;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return latitude + ", " + longitude;
    }

    public int getPotholeCount() {
        return potholeCount;
    }

    public void setPotholeCount(int potholeCount) {
        this.potholeCount = potholeCount;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

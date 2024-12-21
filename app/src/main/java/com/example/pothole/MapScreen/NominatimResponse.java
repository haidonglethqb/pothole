package com.example.pothole.MapScreen;
import com.google.gson.annotations.SerializedName;

public class NominatimResponse {
    @SerializedName("address")
    public Address address;

    public static class Address {
        @SerializedName("road")
        public String road;
    }
}
package com.example.pothole.Other;

public class UserProfile {
    public String name;
    public String email;
    public String phoneNumber;
    public String dateOfBirth;
    public String countryRegion;


    // Default constructor (Firebase yêu cầu)
    public UserProfile() {}

    // Constructor có tham số
    public UserProfile(String name, String email, String phoneNumber, String dateOfBirth, String countryRegion) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.countryRegion = countryRegion;

    }
}

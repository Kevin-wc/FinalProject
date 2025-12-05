package com.example.finalproject;

public class User {
    private String userName;
    private String password;
    private String fullName;
    private String email;
    // Using URL to the profile image stored in our database
    private String profileImageUrl;
    private int points;

    public User() {
    }

    public User(String userName, String password, String fullName, String email,
                String profileImageUrl, int points) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.points = points;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getPoints() {
        return points;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

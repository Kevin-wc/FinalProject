package com.example.finalproject;

public class User {
    private String username;
    private String password;
    private String fullname;
    private String email;
    private int points;

    public User() {
    }

    public String getUserName() {
        return username;
    }

    public String getFullName() {
        return fullname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getPoints() {
        return points;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

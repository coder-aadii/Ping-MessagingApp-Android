package com.messagingapp.ping;

public class Users {
    private String userId;      // Unique ID for the user
    private String profilePic;  // URL or path to the user's profile picture
    private String fullName;    // Full name of the user
    private String email;       // Email address of the user
    private String password;    // User's password (consider securing this)
    private String gender;      // Gender of the user (male/female/other)
    private String dob;         // Date of birth of the user
    private String lastMessage; // Stores the user's last message
    private String status;      // Status of the user (online/offline/active, etc.)

    // Default constructor (required for Firebase)
    public Users() {}

    // Constructor with all user attributes, including userId
    public Users(String userId, String fullName, String email, String password, String profilePic, String gender, String dob, String lastMessage, String status) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
        this.gender = gender;
        this.dob = dob;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    // Getter and Setter methods for each attribute

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


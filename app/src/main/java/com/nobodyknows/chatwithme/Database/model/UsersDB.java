package com.nobodyknows.chatwithme.Database.model;

public class UsersDB {
    public static final String TABLE_NAME = "UsersDB";

    private int id;
    private String name;
    private String status;
    private String contactNumber;
    private String profileUrl;
    private Integer colorCode;
    private String verified;
    private String lastOnline;
    private String currentStatus;
    private String muted;
    private String blocked;
    private String blockedBy;
    private String username;
    private String bio;
    private String dob;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CONTACT_NUMBER = "contactNumber";
    public static final String COLUMN_PROFILE_URL = "profileUrl";
    public static final String COLUMN_COLOR_CODE = "colorCode";
    public static final String COLUMN_VERIFIED = "verified";
    public static final String COLUMN_MUTED = "muted";
    public static final String COLUMN_BLOCKED = "blocked";
    public static final String COLUMN_BLOCKED_BY = "blockedBy";
    public static final String COLUMN_LAST_ONLINE = "lastOnline";
    public static final String COLUMN_CURRENT_STATUS = "currentStatus";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_BIO = "bio";
    public static final String COLUMN_DOB = "dob";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CONTACT_NUMBER +" TEXT NOT NULL UNIQUE,"
                + COLUMN_NAME +" TEXT NOT NULL,"
                + COLUMN_STATUS +" TEXT,"
                + COLUMN_PROFILE_URL +" TEXT,"
                + COLUMN_COLOR_CODE +" INTEGER,"
                + COLUMN_VERIFIED +" TEXT,"
                + COLUMN_MUTED +" TEXT,"
                + COLUMN_BLOCKED +" TEXT,"
                + COLUMN_BLOCKED_BY +" TEXT,"
                + COLUMN_LAST_ONLINE +" TEXT,"
                + COLUMN_USERNAME +" TEXT,"
                + COLUMN_BIO +" TEXT,"
                + COLUMN_DOB +" TEXT,"
                + COLUMN_CURRENT_STATUS +" TEXT"
                +")";
        return CREATE_TABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Integer getColorCode() {
        return colorCode;
    }

    public void setColorCode(Integer colorCode) {
        this.colorCode = colorCode;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getMuted() {
        return muted;
    }

    public void setMuted(String muted) {
        this.muted = muted;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}

package com.nobodyknows.chatwithme.DTOS;

import java.util.ArrayList;

public class VaccineCenter {
    private String centerId;
    private String name;
    private String address;
    private String stateName;
    private String districtName;
    private String blockName;
    private String pincode;
    private float lat;
    private float lon;
    private String from;
    private String to;
    private String feeType;
    private Integer totalAvailable = 0;
    private ArrayList<VaccineSessions> sessions = new ArrayList<>();

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public ArrayList<VaccineSessions> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<VaccineSessions> sessions) {
        this.sessions = sessions;
    }

    public void addSession(VaccineSessions vaccineSessions) {
        this.sessions.add(vaccineSessions);
    }

    public Integer getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Integer totalAvailable) {
        this.totalAvailable = totalAvailable;
    }
}

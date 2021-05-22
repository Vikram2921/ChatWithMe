package com.nobodyknows.chatwithme.DTOS;

import java.util.ArrayList;
import java.util.Date;

public class VaccineSessions {
    private String sessionId;
    private String date;
    private int availableCapacity;
    private int minAgeLimit;
    private String vaccine;
    private int availableDose1;
    private int availableDose2;
    private ArrayList<String> slots = new ArrayList<>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public int getMinAgeLimit() {
        return minAgeLimit;
    }

    public void setMinAgeLimit(int minAgeLimit) {
        this.minAgeLimit = minAgeLimit;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public int getAvailableDose1() {
        return availableDose1;
    }

    public void setAvailableDose1(int availableDose1) {
        this.availableDose1 = availableDose1;
    }

    public int getAvailableDose2() {
        return availableDose2;
    }

    public void setAvailableDose2(int availableDose2) {
        this.availableDose2 = availableDose2;
    }

    public ArrayList<String> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<String> slots) {
        this.slots = slots;
    }

    public void addSlot(String slot) {
       this.slots.add(slot);
    }
}

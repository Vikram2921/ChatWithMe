package com.nobodyknows.chatwithme.Models.Helper;

import java.util.Date;

public class DateTimeModel {
    private int date;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private int second;

    public DateTimeModel() {
        Date todayDate = new Date();
        this.date = todayDate.getDate();
        this.month = todayDate.getMonth();
        this.year = todayDate.getYear();
    }

    public DateTimeModel(int date, int month, int year, int hour, int minute, int second) {
        this.date = date;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}

package com.example.rovermore.planmad.datamodel;

import java.util.Date;

public class Event {

    private int id;
    private String title;
    private String description;
    private int price;
    private Date dtstart;
    private Date dtend;
    private String recurrenceDays;
    private String recurrenceFrequency;
    private String eventLocation;
    private double latitude;
    private double longitude;

    public Event(int id, String title, String description, int price, Date dtstart, Date dtend,
        String recurrenceDays, String recurrenceFrequency, String eventLocation, double latitude, double longitude){
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.recurrenceDays = recurrenceDays;
        this.recurrenceFrequency = recurrenceFrequency;
        this.eventLocation = eventLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public Date getDtstart() {
        return dtstart;
    }

    public Date getDtend() {
        return dtend;
    }

    public String getRecurrenceDays() {
        return recurrenceDays;
    }

    public String getRecurrenceFrequency() {
        return recurrenceFrequency;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

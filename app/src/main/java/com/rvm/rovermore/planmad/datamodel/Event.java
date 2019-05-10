package com.rvm.rovermore.planmad.datamodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
@Entity(tableName = "favEvents")
public class Event implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int idDatabase;
    private int hash;
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
    private String eventType;
    private boolean notification;
    private String link;

    @Ignore
    public Event(int hash, String title, String description, int price, Date dtstart, Date dtend,
        String recurrenceDays, String recurrenceFrequency, String eventLocation, double latitude, double longitude,
                 String eventType, boolean notification, String link){
        this.hash = hash;
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
        this.eventType = eventType;
        this.notification = notification;
        this.link = link;
    }

    public Event(int idDatabase, int hash, String title, String description, int price, Date dtstart, Date dtend,
                 String recurrenceDays, String recurrenceFrequency, String eventLocation, double latitude, double longitude,
                 String eventType, boolean notification, String link){
        this.idDatabase = idDatabase;
        this.hash = hash;
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
        this.eventType = eventType;
        this.notification = notification;
        this.link = link;
    }

    public int getIdDatabase(){return idDatabase;}

    public int getHash() {
        return hash;
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

    public boolean getNotification () { return notification; }

    public String getEventType() { return eventType; }

    public void setNotification (boolean notification){
        this.notification = notification;
    }

    public String getLink() { return link; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idDatabase);
        dest.writeInt(this.hash);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeInt(this.price);
        dest.writeLong(this.dtstart != null ? this.dtstart.getTime() : -1);
        dest.writeLong(this.dtend != null ? this.dtend.getTime() : -1);
        dest.writeString(this.recurrenceDays);
        dest.writeString(this.recurrenceFrequency);
        dest.writeString(this.eventLocation);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.eventType);
        dest.writeString(this.link);

    }

    protected Event(Parcel in) {
        this.idDatabase = in.readInt();
        this.hash = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.price = in.readInt();
        long tmpDtstart = in.readLong();
        this.dtstart = tmpDtstart == -1 ? null : new Date(tmpDtstart);
        long tmpDtend = in.readLong();
        this.dtend = tmpDtend == -1 ? null : new Date(tmpDtend);
        this.recurrenceDays = in.readString();
        this.recurrenceFrequency = in.readString();
        this.eventLocation = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.eventType = in.readString();
        this.link = in.readString();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}

package com.example.rovermore.planmad.datamodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
@Entity(tableName = "favEvents")
public class Event implements Parcelable {

    @PrimaryKey(autoGenerate = true)
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
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
    }

    protected Event(Parcel in) {
        this.id = in.readInt();
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

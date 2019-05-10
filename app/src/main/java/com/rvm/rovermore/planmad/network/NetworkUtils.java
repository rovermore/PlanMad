package com.rvm.rovermore.planmad.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.rvm.rovermore.planmad.datamodel.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Calendar;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_URL = "https://datos.madrid.es/portal/site/egob/menuitem.ac61933d6ee3c31cae77ae7784f1a5a0/?vgnextoid=00149033f2201410VgnVCM100000171f5a0aRCRD&format=json&file=0&filename=206974-0-agenda-eventos-culturales-100&mgmtid=6c0b6d01df986410VgnVCM2000000c205a0aRCRD&preview=full";


    /**
     * Builds the URL used to talk to the MovieDB server.
     */
    public static URL urlBuilder(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * <p>
     * url The URL to fetch the HTTP response from.
     *
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl() throws IOException {
        URL url = urlBuilder(API_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Parses the JSON and saves into a Movie object.
     */
    public static List<Event> parseJson(String json) throws JSONException {
        List<Event> eventList = new ArrayList<>();
        JSONObject resultJson = null;
        String eventTypeList = "LIST OF TYPES: ";
        try {
            resultJson = new JSONObject(json);
        } catch (JSONException e) {
            eventList = null;
            //e.printStackTrace();
            return eventList;


        }
        JSONArray resultArray = resultJson.optJSONArray("@graph");
        for (int i = 0; i < resultArray.length(); i++) {

            JSONObject jsonEvent = resultArray.getJSONObject(i);
            String stringHash = jsonEvent.optString("id");
            int hash = Integer.parseInt(stringHash);
            String type = jsonEvent.optString("@type");
            String eventType = parseEventType(type);
            String title = jsonEvent.optString("title");
            String description = jsonEvent.optString("description");
            int price = jsonEvent.optInt("price");
            String stringDtStart = jsonEvent.optString("dtstart");
            Date dtstart = fromStringToDate(stringDtStart);
            String stringDtEnd = jsonEvent.optString("dtend");
            Date dtend = fromStringToDate(stringDtEnd);
            String link = jsonEvent.optString("link");
            JSONObject jsonRecurrence = jsonEvent.optJSONObject("recurrence");
            String recurrenceDays = null;
            String recurrenceFrequency = null;
            if(jsonRecurrence!=null) {
                recurrenceDays = jsonRecurrence.optString("days");
                recurrenceFrequency = jsonRecurrence.optString("frequency");
            }
            String eventLocation = jsonEvent.optString("event-location");
            JSONObject jsonLocation = jsonEvent.optJSONObject("location");
            double latitude = 0;
            double longitude = 0;
            if(jsonLocation!=null) {
                latitude = jsonLocation.optDouble("latitude");
                longitude = jsonLocation.optDouble("longitude");
            }

            Event event = new Event(hash, title, description, price, dtstart, dtend,recurrenceDays,
                    recurrenceFrequency, eventLocation, latitude, longitude, eventType, false, link);

            Log.v(TAG, "title " + title);
            Log.v(TAG, "id " + hash);
            Log.v(TAG, "RECURRENCE DAYS: " + recurrenceDays);
            Log.v(TAG, "RECURRENCE FREQUENCY " + recurrenceFrequency);
            Log.v(TAG, "LATITUDE " + latitude);
            Log.v(TAG, "LONGITUDE " + longitude);

            eventList.add(event);
            eventTypeList = eventTypeList + "/" + eventType;
            //Log.d(TAG, "FINAL TYPE LIST " + eventTypeList);
        }

        sortEventList(eventList);

        return eventList;
    }

    private static String parseEventType (String type){
        Log.d(TAG, "THE RECEIVED TYPE IS :" + type);
        if(type==""){
            return null;
        }
        type = type + "/";
        String[] firstSplit = type.split("actividades/");
        String firstResult = firstSplit[1];
        String[] secondSplit = firstResult.split("/");
        String eventType = secondSplit[0];
        Log.d(TAG,"the event type is " + eventType);
        return eventType;
    }

    private static void sortEventList(List<Event> eventList) {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getDtstart().compareTo(o2.getDtstart());
            }
        });
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager ConnectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static Date fromStringToDate(String stringDate){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(stringDate);
            Log.v(TAG,"Date parsed is: " + date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String fromDateToString(Date date){

        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String textDate = df.format(date);

        return textDate;
    }

    public static List<Event> getTodayList(List<Event> eventList){
        List<Event> todayEventList = new ArrayList<>();
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateText = df.format(currentDate);

        for (Event event : eventList){
            Date eventDate = event.getDtstart();
            String eventDateText = df.format(eventDate);
            if (eventDateText.equals(currentDateText)){
                todayEventList.add(event);
            }

        }
        return todayEventList;
    }

    public static String translateDaysOfTheweek (String string){
        StringBuilder daysInSpanish = new StringBuilder();
        String[] daysOfTheWeekEnglish = string.split(",");
        Log.d(TAG,"The days of the week on the array strin are: " + daysOfTheWeekEnglish);
        for(int i = 0; i<daysOfTheWeekEnglish.length;i++){
            String currentDay = daysOfTheWeekEnglish[i];
            if(i>0 && i<daysOfTheWeekEnglish.length) daysInSpanish.append(", ");
            if(currentDay.contentEquals("MO")) daysInSpanish.append("L");
            if(currentDay.contentEquals("TU")) daysInSpanish.append("M");
            if(currentDay.contentEquals("WE")) daysInSpanish.append("X");
            if(currentDay.contentEquals("TH")) daysInSpanish.append("J");
            if(currentDay.contentEquals("FR")) daysInSpanish.append("V");
            if(currentDay.contentEquals("SA")) daysInSpanish.append("S");
            if(currentDay.contentEquals("SU")) daysInSpanish.append("D");
        }
        String finalString = daysInSpanish.toString();
        return finalString;
    }
}


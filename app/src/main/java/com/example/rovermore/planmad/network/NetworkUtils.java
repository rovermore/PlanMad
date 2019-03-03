package com.example.rovermore.planmad.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.rovermore.planmad.datamodel.Event;

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
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
        try {
            resultJson = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray resultArray = resultJson.optJSONArray("@graph");
        for (int i = 0; i < resultArray.length(); i++) {

            JSONObject jsonEvent = resultArray.getJSONObject(i);
            String stringId = jsonEvent.optString("id");
            int id = Integer.parseInt(stringId);
            String title = jsonEvent.optString("title");
            String description = jsonEvent.optString("description");
            int price = jsonEvent.optInt("price");
            String stringDtStart = jsonEvent.optString("dtstart");
            Date dtstart = fromStringToDate(stringDtStart);
            String stringDtEnd = jsonEvent.optString("dtend");
            Date dtend = fromStringToDate(stringDtEnd);
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

            Event event = new Event(id, title, description, price, dtstart, dtend,recurrenceDays,
                    recurrenceFrequency, eventLocation, latitude, longitude);

            Log.v(TAG, "title " + title);
            Log.v(TAG, "id " + id);
            Log.v(TAG, "RECURRENCE DAYS: " + recurrenceDays);
            Log.v(TAG, "RECURRENCE FREQUENCY " + recurrenceFrequency);
            Log.v(TAG, "LATITUDE " + latitude);
            Log.v(TAG, "LONGITUDE " + longitude);

            eventList.add(event);
        }

        return eventList;
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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd :mm:ss");
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

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String textDate = df.format(date);

        return textDate;
    }
}


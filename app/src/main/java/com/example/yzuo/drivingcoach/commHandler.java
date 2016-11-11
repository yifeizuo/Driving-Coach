package com.example.yzuo.drivingcoach;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

/**
 * Created by yzuo on 25.8.2015.
 */
public class commHandler {
    private static final String SERVER_ADDRESS = "http://vm0089.virtues.fi:4848/clientconnector";
    private static final String DEFAULT_USER_ID = "user4319";
    //Need userID
    private static final String GET_LAST_TRIP_ID = SERVER_ADDRESS + "/client/lasttripid/";
    private static final String GET_LAST_WEEK_TRIPS = SERVER_ADDRESS + "/client/trips/lastweek/";
    private static final String GET_LAST_MONTH_TRIPS = SERVER_ADDRESS + "/client/trips/lastmonth/";

    //Need userID and tripID
    private static final String GET_TRIP_SEGMENTS = SERVER_ADDRESS + "/client/tripsegments/";
    //Need userID, tripID and segmentStartTime
    private static final String GET_TRIP_ROUTE_GEOMETRY = SERVER_ADDRESS  + "/client/segment/geom/";
    private static final String GET_ROUTE_MARKERS = SERVER_ADDRESS + "/client/segment/routemarkers/";
    private static final String GET_BAD_DRIVING_MARKERS = SERVER_ADDRESS + "/client/segment/markers/";

    private static final String GET_TRIP_STATISTICS = SERVER_ADDRESS + "/client/segment/statistics/";
    private static final String GET_FUEL_ECONOMY_FEEDBACK = SERVER_ADDRESS + "/client/segment/economy/";
    private static final String GET_COMMENTS_REGARDING_TRIP = SERVER_ADDRESS + "/client/segment/advices/";

    //Need factor name and userID
    private static final String GET_FACTOR_LAST_WEEK_HISTORY = SERVER_ADDRESS + "/client/history/";
    private static final String GET_FACTOR_LAST_MONTH_HISTORY = SERVER_ADDRESS + "/client/history/";
    private static final String GET_FACTOR_TIME_INTERVAL_HISTORY = SERVER_ADDRESS + "/client/history/";

    //Put comments, need userID, tripID, segmentStartTime, pointTime
    private  static final String PUT_ANNOTATION = SERVER_ADDRESS + "/client/segment/markers/annotation/";

    public String LAST_TRIP_ID = null;

    private boolean IS_CONNECTED = false;

    private Context _context;

    public interface onResponseListener<T> {
        public void onResponse(T t);
    }

    //Constructor
    public commHandler(Context context) {
        _context = context;
        IS_CONNECTED = isConnected();
    }

    //Check if network is connected
    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public void get(String url, onResponseListener listener) {
        if (IS_CONNECTED == false) {
            Toast.makeText(_context, "No network connection!", Toast.LENGTH_LONG).show();
            return;
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(_context);
        // Request a string response from the provided URL.
        final onResponseListener local_listener = listener; //Must defined as final
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json_object) {
                        local_listener.onResponse(json_object);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        //Toast.makeText(_context, response.statusCode + response.data.toString(), Toast.LENGTH_LONG).show();
                        local_listener.onResponse(new JSONObject());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public void get_array(String url, onResponseListener listener){
        if (IS_CONNECTED == false){
            Toast.makeText(_context, "No network connection!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(_context);
        //Request a string response from the provided URL
        final onResponseListener local_listener = listener; //Must defined as final
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray json_array) {
                        //Actually there will be only one element in the array
                        local_listener.onResponse(json_array);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        //Toast.makeText(_context, response.statusCode + response.data.toString(), Toast.LENGTH_LONG).show();
                        local_listener.onResponse(new JSONArray());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public void get_string(String url, onResponseListener listener) {
        if (IS_CONNECTED == false) {
            Toast.makeText(_context, "No network connection!", Toast.LENGTH_LONG).show();
            return;
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(_context);
        // Request a string response from the provided URL.
        final onResponseListener local_listener = listener; //Must defined as final
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String str) {
                        //Actually there will be only one element in the array
                        local_listener.onResponse(str);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        //Toast.makeText(_context, response.statusCode + response.data.toString(), Toast.LENGTH_LONG).show();
                        local_listener.onResponse("");
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    //Put comments on bad behaviour
    public void put(String url, final String json_string, onResponseListener listener) {
        if (IS_CONNECTED == false) {
            Toast.makeText(_context, "No network connection!", Toast.LENGTH_LONG).show();
            return;
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(_context);
        // Request a string response from the provided URL.
        final onResponseListener local_listener = listener; //Must defined as final
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String str) {
                        //Actually there will be only one element in the array
                        local_listener.onResponse("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        //Toast.makeText(_context, response.statusCode + response.data.toString(), Toast.LENGTH_LONG).show();
                        local_listener.onResponse("" + error.networkResponse.statusCode);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }

            @Override
            public byte[] getBody() {
                final byte[] request_body = json_string.getBytes();
                return request_body;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public void get_last_trip_id(onResponseListener listener) {
        get(GET_LAST_TRIP_ID + DEFAULT_USER_ID, listener);
    }

    public void get_last_trip_segmentStartTime(String tripID, onResponseListener listener) {
        get_array(GET_TRIP_SEGMENTS + DEFAULT_USER_ID + "/" + tripID, listener);
        LAST_TRIP_ID = tripID;
    }

    public void get_latest_last_week_trip_start_time(onResponseListener listener) {
        //Should get a JSON array with multiple elements
        get_array(GET_LAST_WEEK_TRIPS + DEFAULT_USER_ID,listener);
    }

    public void get_latest_last_month_trip_start_time(onResponseListener listener) {
        //Should get a JSON array with multiple elements
        get_array(GET_LAST_MONTH_TRIPS + DEFAULT_USER_ID,listener);
    }

    public void get_trip_route_geometry(String tripID, String segmentStartTime, onResponseListener listener) {
        get_array(GET_TRIP_ROUTE_GEOMETRY + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime, listener);
    }

    public void get_trip_route_markers(String tripID, String segmentStartTime, onResponseListener listener) {
        get_array(GET_ROUTE_MARKERS + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime, listener);
    }

    public void get_trip_bad_driving_markers(String tripID, String segmentStartTime, onResponseListener listener) {
        get_array(GET_BAD_DRIVING_MARKERS + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime, listener);
    }

    public void get_comments_of_last_trip(String tripID, String segmentStartTime, onResponseListener listener) {
        //get();
    }

    public void get_trip_statistics(String tripID, String segmentStartTime, onResponseListener listener) {
        get_string(GET_TRIP_STATISTICS + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime, listener);
    }

    public void get_fuel_economy_feedback(String tripID, String segmentStartTime, onResponseListener listener) {
        get_string(GET_FUEL_ECONOMY_FEEDBACK + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime, listener);
    }

    public void get_comments_regarding_trip(String tripID, String segmentStartTime, onResponseListener listener) {
        get_array(GET_COMMENTS_REGARDING_TRIP + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime, listener);
    }

    public void get_factor_last_week_history(String factor_name, onResponseListener listener) {
        get_string(GET_FACTOR_LAST_WEEK_HISTORY + factor_name + "/lastweek/" + DEFAULT_USER_ID, listener);
    }

    public void get_factor_last_month_history(String factor_name, onResponseListener listener) {
        get_string(GET_FACTOR_LAST_MONTH_HISTORY + factor_name + "/lastmonth/" + DEFAULT_USER_ID, listener);
    }

    public void get_factor_time_interval_history(String factor_name, String fromDate, String toDate, onResponseListener listener) {
        get_string(GET_FACTOR_TIME_INTERVAL_HISTORY + factor_name + "/period/" + DEFAULT_USER_ID + "/" + fromDate + "/" + toDate, listener);
    }

    public void put_comment(String tripID, String segmentStartTime, String pointTime, String comment, onResponseListener listener) {
        String json_string = "{\"comment\": \"" + comment + "\"}";
        put(PUT_ANNOTATION + DEFAULT_USER_ID + "/" + tripID + "/" + segmentStartTime + "/" + pointTime, json_string, listener);
    }

    //String like: 2014-07-28 17:34:06
    public int[] transformDateString(String date_string) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        int[] days = new int[7];
        try {
            Date date = format.parse(date_string);
            cal.setTime(date);

            int first_day = cal.get(Calendar.DAY_OF_MONTH);
            days[0] = first_day;

            cal.add(Calendar.DATE, 1);
            int second_day = cal.get(Calendar.DAY_OF_MONTH);
            days[1] = second_day;

            cal.add(Calendar.DATE, 1);
            int third_day = cal.get(Calendar.DAY_OF_MONTH);
            days[2] = third_day;

            cal.add(Calendar.DATE, 1);
            int fourth_day = cal.get(Calendar.DAY_OF_MONTH);
            days[3] = fourth_day;

            cal.add(Calendar.DATE, 1);
            int fifth_day = cal.get(Calendar.DAY_OF_MONTH);
            days[4] = fifth_day;

            cal.add(Calendar.DATE, 1);
            int sixth_day = cal.get(Calendar.DAY_OF_MONTH);
            days[5] = sixth_day;

            cal.add(Calendar.DATE, 1);
            int seventh_day = cal.get(Calendar.DAY_OF_MONTH);
            days[6] = seventh_day;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    //String like: 2014-07-28 17:34:06
    public Calendar transformDateStringToCalendar(String date_string) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        try {
            Date date = format.parse(date_string);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public Date transformDateStringToDate(String date_string) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = format.parse(date_string);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //To parse String into a Map object with several key-value pairs
    public Calendar[] parseFactorHistoryStringDatesToCalendars(String str) {
        String[] str2 = str.split("\\],\\[");

        //dates array
        String[] str3 = str2[0].split("\\[");

        //Parse dates string
        String[] str4 = str3[1].split(", ");
        Calendar[] cal = new Calendar[str4.length];
        for(int i = 0; i < str4.length; i++){
            cal[i] = transformDateStringToCalendar(str4[i]);
        }
        return cal;
    }

    public Date[] parseFactorHistoryStringDatesToDates(String str) {
        String[] str2 = str.split("\\],\\[");

        //dates array
        String[] str3 = str2[0].split("\\[");

        //Parse dates string
        String[] str4 = str3[1].split(", ");
        Date[] dates = new Date[str4.length];
        for(int i = 0; i < str4.length; i++){
            dates[i] = transformDateStringToDate(str4[i]);
        }
        return dates;
    }

    public Double[] parseFactorHistoryStringValues(String str) {
        String[] str2 = str.split("\\],\\[");
        //Get values
        String[] str5 = str2[1].split("\\]");
        String[] str6 = str5[0].split(", ");

        //Parse values string
        Double[] d = new Double[str6.length];
        for(int i = 0; i < str6.length; i++){
            d[i] = Double.parseDouble(str6[i]);
        }
        return d;
    }
    public Float[] parseFactorHistoryStringValuesToFloats(String str) {
        String[] str2 = str.split("\\],\\[");
        //Get values
        String[] str5 = str2[1].split("\\]");
        String[] str6 = str5[0].split(", ");

        //Parse values string
        Float[] f = new Float[str6.length];
        for(int i = 0; i < str6.length; i++){
            f[i] = Float.parseFloat(str6[i]);
        }
        return f;
    }

    //parse month such as 2 as March, to "03", for server's use
    public static String parseCalendarMonthToTwoDigits(String str) {
        int month = Integer.parseInt(str);
        month += 1;
        if(month < 10) {
            return "0" + month;
        }
        else {
            return "" + month;
        }
    }

    //parse day such as 8 to "08"
    public static String parseCalendarDayToTwoDigits(String str) {
        int day = Integer.parseInt(str);
        if(day < 10) {
            return "0" + day;
        }
        else {
            return "" + day;
        }
    }
}


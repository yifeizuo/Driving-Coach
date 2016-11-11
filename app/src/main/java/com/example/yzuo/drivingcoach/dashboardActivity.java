package com.example.yzuo.drivingcoach;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class dashboardActivity extends AppCompatActivity {

    private TextView commentsRegardingTrip_content, fuelEconomyFeedback_content;

    String bullet = "●  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //First get last trip ID, then onSuccess get segmentStartTime, then onSuccess get comments regarding last trip
        final commHandler mHandler = new commHandler(this);
        final String[] tripID = new String[1];
        final String[] segmentStartTime = new String[1];

        final commHandler.onResponseListener getFuelEconomyCommentsListener = new commHandler.onResponseListener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("")) {
                    fuelEconomyFeedback_content.setText("Nothing to show");
                }
                else {
                    //Do some customized replacement for certain words first, required by server-side. Not recommended indeed.
                    s = s.replaceAll("reduce", "reduce driving with");
                    s = s.replaceAll("speed15_30", "low speed (15-30 km/h)");
                    s = s.replaceAll("speed0_15", "low speed (0-15 km/h)");
                    s = s.replaceAll("speed_osc", "high changes in speed profile");
                    fuelEconomyFeedback_content.setText(bullet + s);
                }
            }
        };
        final commHandler.onResponseListener getCommentsRegardingTrip = new commHandler.onResponseListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                //Different advices for different factor types
                String advice_total = "";
                if (jsonArray.length()==0){
                    advice_total = "Nothing interesting to show";
                }
                else{
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject o = jsonArray.getJSONObject(i);
                            //factor type
                            //String factor = o.getString("factor");
                            String advice = o.getString("advice");

                            advice_total += bullet;
                            advice_total += advice + "\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                commentsRegardingTrip_content.setText(advice_total);
            }
        };
        final commHandler.onResponseListener getLastTripSegmentStartTimeListener = new commHandler.onResponseListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray a) {
                try {
                    segmentStartTime[0] = a.getJSONObject(0).getString("segmentStartTime");
                    //Get fuel economy comments about last trip
                    mHandler.get_fuel_economy_feedback(tripID[0], segmentStartTime[0], getFuelEconomyCommentsListener);

                    //Get comments about last trip
                    mHandler.get_comments_regarding_trip(tripID[0], segmentStartTime[0], getCommentsRegardingTrip);
                    //Toast.makeText(getApplicationContext(), segmentStartTime, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Cannot get segmentStartTime!", Toast.LENGTH_LONG).show();
                }
            }
        };
        commHandler.onResponseListener getLastTripIdListener = new commHandler.onResponseListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                try {
                    tripID[0] = o.getString("lastTripId");
                    mHandler.get_last_trip_segmentStartTime(o.getString("lastTripId"), getLastTripSegmentStartTimeListener);
                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(), "Cannot get lastTripId!", Toast.LENGTH_LONG).show();
                }
            }
        };
        mHandler.get_last_trip_id(getLastTripIdListener);

        init();

    }

    private void init() {
        //Use fuel economy comments and comments regarding the trip (for last trip)
        fuelEconomyFeedback_content = (TextView) findViewById(R.id.fuelEconomyFeedback_content);
        commentsRegardingTrip_content = (TextView) findViewById(R.id.commentsRegardingTrip_content);

    }


}

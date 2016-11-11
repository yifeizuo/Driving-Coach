package com.example.yzuo.drivingcoach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class savedTrips extends AppCompatActivity {


    private ScrollView scrollViewWeek, scrollViewMonth;
    private RadioGroup weekGroup, monthGroup;
    private RadioButton week, month, weekChild, monthChild;
    private int numberWeek = 0, numberMonth = 0;
    private String option_chosen;
    private String tripID_chosen;
    private String segmentStartTime_chosen;

    private Map week_id_segmentStartTime = new HashMap<String, String>();
    private Map month_id_segmentStartTime = new HashMap<String, String>();

    @Override
    public void onBackPressed() {
        int checkedId = -1;
        if(option_chosen.equals("lastweek")) {
            if (weekGroup.getCheckedRadioButtonId() == -1) {
                checkedId = monthGroup.getCheckedRadioButtonId();
                option_chosen = "lastmonth";
            }
            else {
                checkedId = weekGroup.getCheckedRadioButtonId();
            }
        }
        else if (option_chosen.equals("lastmonth")) {
            if (monthGroup.getCheckedRadioButtonId() == -1) {
                checkedId = weekGroup.getCheckedRadioButtonId();
                option_chosen = "lastweek";
            }
            else {
                checkedId = monthGroup.getCheckedRadioButtonId();
            }
        }

        tripID_chosen = "" + checkedId;
        if(option_chosen.equals("lastweek")) {
            segmentStartTime_chosen = (String) week_id_segmentStartTime.get(tripID_chosen);
        }
        else if (option_chosen.equals("lastmonth")) {
            segmentStartTime_chosen = (String) month_id_segmentStartTime.get(tripID_chosen);
        }

        Intent intent = new Intent();
        intent.putExtra("option", option_chosen);
        intent.putExtra("tripID", tripID_chosen);
        intent.putExtra("segmentStartTime", segmentStartTime_chosen);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_trips);

        final commHandler mHandler = new commHandler(this);
        //Latest last week trips, get saved trips start time
        final commHandler.onResponseListener getLastWeekTripsStartTime = new commHandler.onResponseListener<JSONArray>() {

            @Override
            public void onResponse(JSONArray a) {
                numberWeek = a.length();
                String[] start_time_array = new String[numberWeek];
                String[] trip_id_array = new String[numberWeek];
                for(int i = 0; i < numberWeek; i++) {
                    try {
                        trip_id_array[i] = a.getJSONObject(i).getString("tripID");
                        start_time_array[i] = a.getJSONObject(i).getString("tripStartTime");
                        String segmentStartTime = a.getJSONObject(i).getString("segmentStartTime");
                        week_id_segmentStartTime.put(trip_id_array[i], segmentStartTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                addWeekRadioButtons(trip_id_array, start_time_array);
            }
        };

        final commHandler.onResponseListener getLastMonthTripsStartTime = new commHandler.onResponseListener<JSONArray>() {

            @Override
            public void onResponse(JSONArray a) {
                numberMonth = a.length();
                String[] trip_id_array = new String[numberMonth];
                String[] start_time_array = new String[numberMonth];
                for(int i = 0; i < numberMonth; i++) {
                    try {
                        trip_id_array[i] = a.getJSONObject(i).getString("tripID");
                        start_time_array[i] = a.getJSONObject(i).getString("tripStartTime");
                        String segmentStartTime = a.getJSONObject(i).getString("segmentStartTime");
                        month_id_segmentStartTime.put(trip_id_array[i], segmentStartTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                addMonthRadioButtons(trip_id_array, start_time_array);
            }
        };

        mHandler.get_latest_last_week_trip_start_time(getLastWeekTripsStartTime);
        mHandler.get_latest_last_month_trip_start_time(getLastMonthTripsStartTime);

        init();

        weekOrMonthOnClickListener();
    }

    private void init() {
        //Week and Month choose
        week = (RadioButton) findViewById(R.id.week);
        month = (RadioButton) findViewById(R.id.month);

        //when press week, all the list of trips of last week is ordered in a radio group
        weekGroup = (RadioGroup) findViewById(R.id.weekGroup);
        monthGroup = (RadioGroup) findViewById(R.id.monthGroup);

        //make the radio group scrollable
        scrollViewWeek = (ScrollView) findViewById(R.id.scrollViewWeek);
        scrollViewMonth = (ScrollView) findViewById(R.id.scrollViewMonth);

        //Decide according to extras put by mapActivity
        Intent intent = getIntent();
        if (intent.getStringExtra("option").equals("lastweek")){
            week.setChecked(true);
            option_chosen = "lastweek";
            scrollViewWeek.setVisibility(View.VISIBLE);
            scrollViewMonth.setVisibility(View.GONE);
        }
        else {
            month.setChecked(true);
            option_chosen = "lastmonth";
            scrollViewWeek.setVisibility(View.GONE);
            scrollViewMonth.setVisibility(View.VISIBLE);
        }
        tripID_chosen = intent.getStringExtra("tripID");

    }


    private void addWeekRadioButtons(String[] trip_id, String[] trip_start_time) {
        boolean DEFAULT_CHOICE = false;
        if (tripID_chosen.equals("-1")) {
            DEFAULT_CHOICE = true;
        }
        for (int i = 1; i <= numberWeek; i++)
        {
            //Remember to apply style for dynamic radio buttons as style="@style/timeSpanRadioGroupStyle"
            //weekChild = new RadioButton(this);
            weekChild = (RadioButton) getLayoutInflater().inflate(R.layout.template_radio_button, null);
            weekChild.setId(Integer.parseInt(trip_id[i-1]));
            //weekChild.setText("Radio " + weekChild.getId());
            weekChild.setText(trip_start_time[i-1]);

            //Set text size
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            float dp = 10f;
            float fpixels = metrics.density * dp;
            int pixels = (int) (fpixels + 0.5f);
            weekChild.setTextSize(pixels);
            weekGroup.addView(weekChild);

            if (option_chosen.equals("lastweek") && DEFAULT_CHOICE == true && i == 1) {
                weekChild.setChecked(true);
            }
            else if (option_chosen.equals("lastweek") && tripID_chosen.equals(trip_id[i-1])) {
                weekChild.setChecked(true);
            }

        }
        weekRadioButtonsOnClickListener();
    }

    private void addMonthRadioButtons(String[] trip_id, String[] trip_start_time) {
        boolean DEFAULT_CHOICE = false;
        if (tripID_chosen.equals("-1")) {
            DEFAULT_CHOICE = true;
        }
        for (int i = 1; i <= numberMonth; i++)
        {
            monthChild = (RadioButton) getLayoutInflater().inflate(R.layout.template_radio_button, null);
            monthChild.setId(Integer.parseInt(trip_id[i-1]));
            //monthChild.setText("Radio " + monthChild.getId());
            monthChild.setText(trip_start_time[i-1]);
            //Set text size
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            float dp = 10f;
            float fpixels = metrics.density * dp;
            int pixels = (int) (fpixels + 0.5f);
            monthChild.setTextSize(pixels);
            monthGroup.addView(monthChild);

            if (option_chosen.equals("lastmonth") && DEFAULT_CHOICE == true && i == 1) {
                monthChild.setChecked(true);
            }
            else if (option_chosen.equals("lastmonth") && tripID_chosen.equals(trip_id[i-1])) {
                monthChild.setChecked(true);
            }
        }
        monthRadioButtonsOnClickListener();
    }


    private void weekOrMonthOnClickListener() {
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make radio group week visible
                scrollViewWeek.setVisibility(View.VISIBLE);
                scrollViewMonth.setVisibility(View.GONE);
                option_chosen = "lastweek";
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make radio group month visible
                scrollViewMonth.setVisibility(View.VISIBLE);
                scrollViewWeek.setVisibility(View.GONE);
                option_chosen = "lastmonth";
            }
        });
    }

    private void weekRadioButtonsOnClickListener() {
        weekGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onBackPressed();
            }
        });

    }

    private void monthRadioButtonsOnClickListener() {
        monthGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onBackPressed();
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
}

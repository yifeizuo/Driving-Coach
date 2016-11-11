package com.example.yzuo.drivingcoach;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class timespan extends AppCompatActivity {

    private Button startTime, endTime;
    private Calendar myCalendarStart, myCalendarEnd;
    private RadioGroup timeSpanRadioGroup;
    private RadioButton lastWeek, lastMonth, selectInterval;
    private String option;
    private boolean isCalendarStartSet = false;
    private boolean isCalendarEndSet = false;

    DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            //If end time is earlier than start time, return and alert user to select earlier start time
            if(isCalendarEndSet) {
                if (year > myCalendarEnd.get(Calendar.YEAR) ||
                        (year == myCalendarEnd.get(Calendar.YEAR) && monthOfYear > myCalendarEnd.get(Calendar.MONTH)) ||
                        (year == myCalendarEnd.get(Calendar.YEAR) && monthOfYear == myCalendarEnd.get(Calendar.MONTH) && dayOfMonth > myCalendarEnd.get(Calendar.DAY_OF_MONTH))) {
                    Toast.makeText(getApplicationContext(), "Start time should be earlier than end time!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //Store previous data
            int _year = myCalendarStart.get(Calendar.YEAR);
            int _monthOfYear = myCalendarStart.get(Calendar.MONTH);
            int _dayOfMonth = myCalendarStart.get(Calendar.DAY_OF_MONTH);

            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, monthOfYear);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //Calculate if the time span is less than 90 days. If not, return and alert user to select correct end time
            if(isCalendarEndSet) {
                long diff = myCalendarEnd.getTimeInMillis() - myCalendarStart.getTimeInMillis();
                if (TimeUnit.MILLISECONDS.toDays(diff) > 90) {
                    Toast.makeText(getApplicationContext(), "Time span should be less than 90 days!", Toast.LENGTH_SHORT).show();
                    //Rollback to previous state
                    myCalendarStart.set(Calendar.YEAR, _year);
                    myCalendarStart.set(Calendar.MONTH, _monthOfYear);
                    myCalendarStart.set(Calendar.DAY_OF_MONTH, _dayOfMonth);
                    return;
                }
            }
            updateLabel(startTime, myCalendarStart);
            isCalendarStartSet = true;

            if(isCalendarEndSet) {
                onBackPressed();
            }
        }
    };

    DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            //If user selects end time first and let start time null, return and alert user to select start time first
            if (startTime.getText() == "") {
                Toast.makeText(getApplicationContext(), "Select start time first!", Toast.LENGTH_SHORT).show();
                return;
            }
            //If end time is earlier than start time, return and alert user to select later time
            if (year < myCalendarStart.get(Calendar.YEAR) ||
                    (year == myCalendarStart.get(Calendar.YEAR) && monthOfYear < myCalendarStart.get(Calendar.MONTH)) ||
                    (year == myCalendarStart.get(Calendar.YEAR) && monthOfYear == myCalendarStart.get(Calendar.MONTH) && dayOfMonth < myCalendarStart.get(Calendar.DAY_OF_MONTH))) {
                Toast.makeText(getApplicationContext(), "End time should be later than start time!", Toast.LENGTH_SHORT).show();
                return;
            }

            int _year = myCalendarEnd.get(Calendar.YEAR);
            int _monthOfYear = myCalendarEnd.get(Calendar.MONTH);
            int _dayOfMonth = myCalendarEnd.get(Calendar.DAY_OF_MONTH);

            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //Calculate if the time span is less than 90 days. If not, return and alert user to select correct end time
            long diff = myCalendarEnd.getTimeInMillis() - myCalendarStart.getTimeInMillis();
            if (TimeUnit.MILLISECONDS.toDays(diff) > 90) {
                Toast.makeText(getApplicationContext(), "Time span should be less than 90 days!", Toast.LENGTH_SHORT).show();
                //Rollback to previous state
                myCalendarEnd.set(Calendar.YEAR, _year);
                myCalendarEnd.set(Calendar.MONTH, _monthOfYear);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, _dayOfMonth);

                return;
            }
            updateLabel(endTime, myCalendarEnd);
            isCalendarEndSet = true;
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("option", option);
        if(option.equals("3")) {
            if(!isCalendarStartSet || !isCalendarEndSet) {
                Toast.makeText(getApplicationContext(), "You should set the time interval!", Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra("start_year", "" + myCalendarStart.get(Calendar.YEAR));
            intent.putExtra("start_month", "" + myCalendarStart.get(Calendar.MONTH));
            intent.putExtra("start_day", "" + myCalendarStart.get(Calendar.DAY_OF_MONTH));
            intent.putExtra("end_year", "" + myCalendarEnd.get(Calendar.YEAR));
            intent.putExtra("end_month", "" + myCalendarEnd.get(Calendar.MONTH));
            intent.putExtra("end_day", "" + myCalendarEnd.get(Calendar.DAY_OF_MONTH));
        }
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timespan);

        initCalender();
        init();

        timeSpanRadioGroupListener();
    }

    private void timeSpanRadioGroupListener() {
        timeSpanRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.lastWeek:
                        startTime.setVisibility(View.INVISIBLE);
                        endTime.setVisibility(View.INVISIBLE);
                        option = "1";
                        break;
                    case R.id.lastMonth:
                        startTime.setVisibility(View.INVISIBLE);
                        endTime.setVisibility(View.INVISIBLE);
                        option = "2";
                        break;
                    case R.id.selectInterval:
                        startTime.setVisibility(View.VISIBLE);
                        endTime.setVisibility(View.VISIBLE);
                        startTime.setEnabled(true);
                        endTime.setEnabled(true);
                        option = "3";
                        Toast.makeText(getApplicationContext(),"Time span should be less than 90 days!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void init() {
        timeSpanRadioGroup = (RadioGroup) findViewById(R.id.timeSpanRadioGroup);
        lastWeek = (RadioButton) findViewById(R.id.lastWeek);
        lastMonth = (RadioButton) findViewById(R.id.lastMonth);
        selectInterval = (RadioButton) findViewById(R.id.selectInterval);
        startTime = (Button) findViewById(R.id.startingTime);
        endTime = (Button) findViewById(R.id.endingTime);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(timespan.this, dateStart,
                        myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(timespan.this, dateEnd,
                        myCalendarEnd.get(Calendar.YEAR),
                        myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Intent intent = getIntent();
        option = intent.getStringExtra("option");
        if(option.equals("1")) {
            lastWeek.setChecked(true);
            startTime.setVisibility(View.INVISIBLE);
            endTime.setVisibility(View.INVISIBLE);

            startTime.setEnabled(false);
            endTime.setEnabled(false);
        }
        else if(option.equals("2")) {
            lastMonth.setChecked(true);
            startTime.setVisibility(View.INVISIBLE);
            endTime.setVisibility(View.INVISIBLE);

            startTime.setEnabled(false);
            endTime.setEnabled(false);
        }
        else if(option.equals("3")) {
            selectInterval.setChecked(true);
            startTime.setVisibility(View.VISIBLE);
            endTime.setVisibility(View.VISIBLE);
            String y = intent.getStringExtra("start_year");
            int yy = Integer.parseInt(y);
            myCalendarStart.set(Calendar.YEAR, yy);
            myCalendarStart.set(Calendar.MONTH, Integer.parseInt(intent.getStringExtra("start_month")));
            myCalendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(intent.getStringExtra("start_day")));
            myCalendarEnd.set(Calendar.YEAR, Integer.parseInt(intent.getStringExtra("end_year")));
            myCalendarEnd.set(Calendar.MONTH, Integer.parseInt(intent.getStringExtra("end_month")));
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(intent.getStringExtra("end_day")));
            updateLabel(startTime, myCalendarStart);
            updateLabel(endTime, myCalendarEnd);

            isCalendarEndSet = true;
            isCalendarStartSet = true;

            startTime.setEnabled(true);
            endTime.setEnabled(true);
        }

        //Set listeners for options
        lastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initCalender() {
        myCalendarStart = Calendar.getInstance();
        myCalendarEnd = Calendar.getInstance();

        isCalendarStartSet = false;
        isCalendarEndSet = false;
    }

    private void updateLabel(Button button, Calendar calendar) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        button.setText(sdf.format(calendar.getTime()));
    }
}






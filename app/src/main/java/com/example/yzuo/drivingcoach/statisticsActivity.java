package com.example.yzuo.drivingcoach;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yzuo.drivingcoach.statisticsActivitySwipe.ViewPagerAdapterDriving;
import com.example.yzuo.drivingcoach.statisticsActivitySwipe.ViewPagerAdapterRoute;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class statisticsActivity extends AppCompatActivity {

    private RadioGroup mapRadioGroup;
    private RadioButton Route, Driving;
    private ImageButton timespan;

    private TextView option_title;

    private ViewPagerAdapterRoute mAdapterRoute;
    private ViewPagerAdapterDriving mAdapterDriving;
    private NonSwipeableViewPager mViewPagerRoute, mViewPagerDriving;

    private Button btn_prev_route, btn_next_route;
    private Button btn_prev_driving, btn_next_driving;

    private static final int SELECT_INTERVAL_REQUEST_CODE = 1;
    private String option_chosen = "1"; //By default it is last week. 1 is last week. 2 is last month. 3 is custom interval
    private String start_year, start_month, start_day;
    private String end_year, end_month, end_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        option_title = (TextView) findViewById(R.id.option_title);

        init();

        timeSpanClickListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_INTERVAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                //First page is selected by default, so prev button should be disabled
                btn_prev_route.setEnabled(false);
                btn_prev_driving.setEnabled(false);
                btn_next_route.setEnabled(true);
                btn_next_driving.setEnabled(true);


                option_chosen = data.getStringExtra("option");
                if (option_chosen.equals("1")) {
                    mAdapterDriving.setOption("1");
                    mViewPagerDriving.setAdapter(mAdapterDriving);

                    mAdapterRoute.setOption("1");
                    mViewPagerRoute.setAdapter(mAdapterRoute);

                    setupOptionTitle("1");
                }
                if (option_chosen.equals("2")) {
                    //mAdapterDriving.notifyDataSetChanged();
                    mAdapterRoute.setOption("2");
                    mViewPagerRoute.setAdapter(mAdapterRoute);
                    mAdapterDriving.setOption("2");
                    mViewPagerDriving.setAdapter(mAdapterDriving);

                    setupOptionTitle("2");

                }
                else if (option_chosen.equals("3")) {
                    start_year = data.getStringExtra("start_year");
                    start_month = data.getStringExtra("start_month");
                    start_day = data.getStringExtra("start_day");
                    end_year = data.getStringExtra("end_year");
                    end_month = data.getStringExtra("end_month");
                    end_day = data.getStringExtra("end_day");

                    mAdapterDriving.setOption("3");
                    //YYYYMMDDHHmm
                    //Remember month in Calendar is 1 less
                    mAdapterDriving.setFromDateTime(start_year
                            + commHandler.parseCalendarMonthToTwoDigits(start_month)
                            + commHandler.parseCalendarDayToTwoDigits(start_day)
                            + "0000");
                    mAdapterDriving.setToDateTime(end_year
                            + commHandler.parseCalendarMonthToTwoDigits(end_month)
                            + commHandler.parseCalendarDayToTwoDigits(end_day)
                            + "0000");
                    mViewPagerDriving.setAdapter(mAdapterDriving);


                    mAdapterRoute.setOption("3");
                    //YYYYMMDDHHmm
                    //Remember month in Calendar is 1 less
                    mAdapterRoute.setFromDateTime(start_year
                            + commHandler.parseCalendarMonthToTwoDigits(start_month)
                            + commHandler.parseCalendarDayToTwoDigits(start_day)
                            + "0000");
                    mAdapterRoute.setToDateTime(end_year
                            + commHandler.parseCalendarMonthToTwoDigits(end_month)
                            + commHandler.parseCalendarDayToTwoDigits(end_day)
                            + "0000");
                    mViewPagerRoute.setAdapter(mAdapterRoute);

                    setupOptionTitle("3",
                            commHandler.parseCalendarDayToTwoDigits(start_day) + "/" +
                            commHandler.parseCalendarMonthToTwoDigits(start_month) + "/" +
                            start_year,
                            commHandler.parseCalendarDayToTwoDigits(end_day) + "/" +
                            commHandler.parseCalendarMonthToTwoDigits(end_month) + "/" +
                            end_year);
                }
                //Get stats and draw
            }
        }
    }

    private void initButton() {
        btn_prev_route = (Button) findViewById(R.id.btn_prev_route);
        btn_next_route = (Button) findViewById(R.id.btn_next_route);
        btn_prev_driving = (Button) findViewById(R.id.btn_prev_driving);
        btn_next_driving = (Button) findViewById(R.id.btn_next_driving);

        //First page is selected by default, so prev button should be disabled
        btn_prev_route.setEnabled(false);
        btn_prev_driving.setEnabled(false);

        //Set onclick listeners
        btn_prev_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerRoute.setCurrentItem(mViewPagerRoute.getCurrentItem() - 1, true);
                int page = mViewPagerRoute.getCurrentItem();
                if (page == 0) {
                    btn_prev_route.setEnabled(false);
                    btn_next_route.setEnabled(true);
                } else {
                    btn_prev_route.setEnabled(true);
                    btn_next_route.setEnabled(true);
                }
            }
        });
        btn_next_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerRoute.setCurrentItem(mViewPagerRoute.getCurrentItem() + 1, true);
                int page = mViewPagerRoute.getCurrentItem();
                //9 pages for route
                if (page == 8) {
                    btn_prev_route.setEnabled(true);
                    btn_next_route.setEnabled(false);
                } else {
                    btn_prev_route.setEnabled(true);
                    btn_next_route.setEnabled(true);
                }
            }
        });
        btn_prev_driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerDriving.setCurrentItem(mViewPagerDriving.getCurrentItem() - 1, true);
                int page = mViewPagerDriving.getCurrentItem();
                if (page == 0) {
                    btn_prev_driving.setEnabled(false);
                    btn_next_driving.setEnabled(true);
                } else {
                    btn_prev_driving.setEnabled(true);
                    btn_next_driving.setEnabled(true);
                }
            }
        });
        btn_next_driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerDriving.setCurrentItem(mViewPagerDriving.getCurrentItem() + 1, true);
                int page = mViewPagerDriving.getCurrentItem();
                if (page == 6) {
                    btn_prev_driving.setEnabled(true);
                    btn_next_driving.setEnabled(false);
                } else {
                    btn_prev_driving.setEnabled(true);
                    btn_next_driving.setEnabled(true);
                }
            }
        });
    }

    private void init() {
        //get radio group of Route and Driving
        mapRadioGroup = (RadioGroup) findViewById(R.id.radio_group_list_selector);

        //listener of "Route" and "Driving"
        checkListener checkRadio = new checkListener();
        mapRadioGroup.setOnCheckedChangeListener(checkRadio);

        //set radio buttons of Route and Driving
        Route = (RadioButton) findViewById(R.id.route);
        Driving = (RadioButton) findViewById(R.id.driving);

        //Set up view by default it is last week
        mAdapterRoute = new ViewPagerAdapterRoute(getApplicationContext(), getSupportFragmentManager(), "1");
        mViewPagerRoute = (NonSwipeableViewPager) findViewById(R.id.pager_route);
        mViewPagerRoute.setAdapter(mAdapterRoute);
        setupOptionTitle("1");

        mAdapterDriving = new ViewPagerAdapterDriving(getApplicationContext(), getSupportFragmentManager(), "1");
        mViewPagerDriving = (NonSwipeableViewPager) findViewById(R.id.pager_driving);
        mViewPagerDriving.setAdapter(mAdapterDriving);

        initButton();

        //Set tabs first, then listener will work if certain page is set checked
        setTab();

        mViewPagerRoute.setCurrentItem(0);
        mViewPagerDriving.setCurrentItem(0);

        mViewPagerRoute.setPageMargin(0);

        Route.setChecked(true);
    }

    private void setupOptionTitle(String option) {
        if(option.equals("1")) {
            option_title.setText("Trips of last week");
        }
        else if (option.equals("2")) {
            option_title.setText("Trips of last month");
        }
    }

    private  void setupOptionTitle(String option, String from_date, String to_date) {
        if(option.equals("3")) {
            option_title.setText("Trips from " + from_date + " to " + to_date);
        }
    }

    private void setTab() {
        mViewPagerRoute.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPagerDriving.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void timeSpanClickListener() {
        timespan= (ImageButton) findViewById(R.id.timespan);
        timespan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(statisticsActivity.this,
                        timespan.class);
                myIntent.putExtra("option", option_chosen);
                if (option_chosen.equals("3")) {
                    myIntent.putExtra("start_year", start_year);
                    myIntent.putExtra("start_month", start_month);
                    myIntent.putExtra("start_day", start_day);
                    myIntent.putExtra("end_year", end_year);
                    myIntent.putExtra("end_month", end_month);
                    myIntent.putExtra("end_day", end_day);
                }
                startActivityForResult(myIntent, SELECT_INTERVAL_REQUEST_CODE);
            }
        });
    }




    //checkListener
    public class checkListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            switch (checkedId) {
                case R.id.route:
                    mViewPagerRoute.setVisibility(View.VISIBLE);
                    mViewPagerDriving.setVisibility(View.GONE);
                    showButton(true, btn_prev_route);
                    showButton(true, btn_next_route);
                    showButton(false, btn_prev_driving);
                    showButton(false, btn_next_driving);
                    break;
                case R.id.driving:
                    mViewPagerRoute.setVisibility(View.GONE);
                    mViewPagerDriving.setVisibility(View.VISIBLE);
                    showButton(false, btn_prev_route);
                    showButton(false, btn_next_route);
                    showButton(true, btn_prev_driving);
                    showButton(true, btn_next_driving);
                    break;
            }
        }
    }

    private void showButton(boolean MAKE_BUTTON_VISIBLE, Button btn) {
        btn.setVisibility(MAKE_BUTTON_VISIBLE == true ? View.VISIBLE : View.GONE);
    }

    private void setButtonSize(Button btn, int width, int height) {
        android.view.ViewGroup.LayoutParams params = btn.getLayoutParams();
        params.width = width;
        params.height = height;
        btn.setLayoutParams(params);
    }


}

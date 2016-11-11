package com.example.yzuo.drivingcoach.statisticsActivitySwipe;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yzuo.drivingcoach.Pair;
import com.example.yzuo.drivingcoach.R;
import com.example.yzuo.drivingcoach.commHandler;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yifei on 8/10/2015.
 */
public class num_crossings_ped extends Fragment {
    static Context _context;
    static String _option;
    static String _fromDateTime;
    static String _toDateTime;

    //Option 3
    public static Fragment newInstance(Context context, String option, String fromDateTime, String toDateTime) {

        _context = context;
        _option = option;
        _fromDateTime = fromDateTime;
        _toDateTime = toDateTime;
        //This should be below setting up parameters for Fragment instance
        num_crossings_ped f = new num_crossings_ped();

        return f;
    }

    public static Fragment newInstance(Context context, String option) {

        _context = context;
        _option = option;
        //This should be below setting up parameters for Fragment instance
        num_crossings_ped f = new num_crossings_ped();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.num_crossings_ped, null);

        final commHandler mHandler = new commHandler(_context);
        //Last week
        if(_option.equals("1")) {
            final commHandler.onResponseListener getStopFactorLastWeekListener = new commHandler.onResponseListener<String>() {

                @Override
                public void onResponse(String s) {
                    if (s.equals("")) {
                        Toast.makeText(_context, "No data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GraphView graph = (GraphView) root.findViewById(R.id.graph);
                    Date[] dates = mHandler.parseFactorHistoryStringDatesToDates(s);
                    Double[] d = mHandler.parseFactorHistoryStringValues(s);
                    final Calendar oldest_day = Calendar.getInstance();
                    final Calendar latest_day = Calendar.getInstance();

                    //Sort the dates and need to know corresponding indices for values in "d"
                    final Pair[] p = new Pair[dates.length];
                    for(int i = 0; i < dates.length; i++) {
                        long time = dates[i].getTime();
                        p[i] = new Pair(i, dates[i].getTime());
                    }
                    //Ascending sort
                    Arrays.sort(p);

                    DataPoint[] dp = new DataPoint[p.length];
                    for(int i = p.length - 1; i >= 0; i--) {
                        dp[i] = new DataPoint(p[i].value, d[p[i].index]);
                    }

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
                    PointsGraphSeries<DataPoint> points_series = new PointsGraphSeries<DataPoint>(dp);
                    graph.addSeries(series);
                    graph.addSeries(points_series);
                    points_series.setSize(5);
                    //Active color
                    points_series.setColor(Color.rgb(39, 201, 162));
                    //Theme color
                    series.setColor(Color.rgb(76, 31, 33));
                    graph.setTitle("Number of pedestrian crossings per meter");
                    points_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPointInterface) {
                            Date date = new Date();
                            date.setTime((long) dataPointInterface.getX());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Toast.makeText(_context, String.format("%-10s", "Date:") + dateFormat.format(date) + "\n" + String.format("%-10s", "Value:") + String.format("%.2f", dataPointInterface.getY()), Toast.LENGTH_LONG).show();
                        }
                    });

                    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis((long) value);
                                if (cal.get(Calendar.DAY_OF_MONTH) < oldest_day.get(Calendar.DAY_OF_MONTH) || cal.get(Calendar.DAY_OF_MONTH) > latest_day.get(Calendar.DAY_OF_MONTH)) {
                                    //The extra day should have no label, just to make sure all data will be displayed
                                    return "";
                                } else {
                                    return "" + cal.get(Calendar.DAY_OF_MONTH);
                                }
                            }
                            return super.formatLabel(value, isValueX);
                        }
                    });
                    latest_day.setTimeInMillis(p[p.length - 1].value);
                    //Add 1 more day to display the data completely, but the label should not be displayed
                    latest_day.add(Calendar.DATE, 1);
                    graph.getViewport().setMaxX(latest_day.getTime().getTime());
                    //Reverse cal to reuse it
                    latest_day.add(Calendar.DATE, -1);
                    //Compare the minus 6 date and the oldest date from data, if data date is still older, we need to use that date as min, so that this data point will not be lost
                    oldest_day.setTimeInMillis(latest_day.getTimeInMillis());
                    oldest_day.add(Calendar.DATE, -6);
                    //Oldest trip
                    Calendar trip = Calendar.getInstance();
                    trip.setTimeInMillis(p[0].value);
                    if(oldest_day.compareTo(trip) > 0) {
                        //data date is still older
                        graph.getViewport().setMinX(p[0].value);
                    }
                    else{
                        graph.getViewport().setMinX(oldest_day.getTime().getTime());
                    }

                    graph.getViewport().setXAxisBoundsManual(true);

                    graph.getGridLabelRenderer().setNumHorizontalLabels(8);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getGridLabelRenderer().setHighlightZeroLines(false);
                }
            };
            mHandler.get_factor_last_week_history("num_crossings_ped", getStopFactorLastWeekListener);
        }
        //Last month
        else if (_option.equals("2")) {
            final commHandler.onResponseListener getStopFactorLastMonthListener = new commHandler.onResponseListener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.equals("")) {
                        Toast.makeText(_context, "No data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GraphView graph = (GraphView) root.findViewById(R.id.graph);
                    Date[] dates = mHandler.parseFactorHistoryStringDatesToDates(s);
                    Double[] d = mHandler.parseFactorHistoryStringValues(s);

                    //Sort the dates and need to know corresponding indices for values in "d"
                    Pair[] p = new Pair[dates.length];
                    for(int i = 0; i < dates.length; i++) {
                        p[i] = new Pair(i, dates[i].getTime());
                    }
                    //Ascending sort
                    Arrays.sort(p);

                    DataPoint[] dp = new DataPoint[p.length];
                    for(int i = p.length - 1; i >= 0; i--) {
                        dp[i] = new DataPoint(p[i].value, d[p[i].index]);
                    }

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
                    PointsGraphSeries<DataPoint> points_series = new PointsGraphSeries<DataPoint>(dp);
                    points_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPointInterface) {
                            Date date = new Date();
                            date.setTime((long) dataPointInterface.getX());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Toast.makeText(_context, String.format("%-10s", "Date:") + dateFormat.format(date) + "\n" + String.format("%-10s", "Value:") + String.format("%.2f", dataPointInterface.getY()), Toast.LENGTH_LONG).show();
                        }
                    });

                    graph.addSeries(series);
                    graph.addSeries(points_series);

                    //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis((long) value);
                                int day = cal.get(Calendar.DAY_OF_MONTH);
                                Date date = cal.getTime();
                                return "" + day;
                            }
                            return super.formatLabel(value, isValueX);
                        }
                    });

                    points_series.setSize(5);
                    //Active color
                    points_series.setColor(Color.rgb(39, 201, 162));
                    //Theme color
                    series.setColor(Color.rgb(76, 31, 33));
                    graph.setTitle("Number of pedestrian crossings per meter");

                    graph.getViewport().setMinX(p[0].value);
                    graph.getViewport().setMaxX(p[p.length - 1].value);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getGridLabelRenderer().setHighlightZeroLines(false);
                    //graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5 because of the space
                }
            };
            mHandler.get_factor_last_month_history("num_crossings_ped", getStopFactorLastMonthListener);
        }
        //Custom time interval
        else if (_option.equals("3")) {
            final commHandler.onResponseListener getStopFactorTimeIntervalListener = new commHandler.onResponseListener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.equals("")) {
                        Toast.makeText(_context, "No data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GraphView graph = (GraphView) root.findViewById(R.id.graph);
                    Date[] dates = mHandler.parseFactorHistoryStringDatesToDates(s);
                    Double[] d = mHandler.parseFactorHistoryStringValues(s);

                    //Sort the dates and need to know corresponding indices for values in "d"
                    Pair[] p = new Pair[dates.length];
                    for(int i = 0; i < dates.length; i++) {
                        p[i] = new Pair(i, dates[i].getTime());
                    }
                    //Ascending sort
                    Arrays.sort(p);

                    DataPoint[] dp = new DataPoint[p.length];
                    for(int i = p.length - 1; i >= 0; i--) {
                        dp[i] = new DataPoint(p[i].value, d[p[i].index]);
                    }

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
                    PointsGraphSeries<DataPoint> points_series = new PointsGraphSeries<DataPoint>(dp);
                    points_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPointInterface) {
                            Date date = new Date();
                            date.setTime((long) dataPointInterface.getX());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Toast.makeText(_context, String.format("%-10s", "Date:") + dateFormat.format(date) + "\n" + String.format("%-10s", "Value:") + String.format("%.2f", dataPointInterface.getY()), Toast.LENGTH_LONG).show();
                        }
                    });

                    graph.addSeries(series);
                    graph.addSeries(points_series);

                    //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis((long) value);
                                int day = cal.get(Calendar.DAY_OF_MONTH);
                                Date date = cal.getTime();
                                return "" + day;
                            }
                            return super.formatLabel(value, isValueX);
                        }
                    });

                    points_series.setSize(5);
                    //Active color
                    points_series.setColor(Color.rgb(39, 201, 162));
                    //Theme color
                    series.setColor(Color.rgb(76, 31, 33));
                    graph.setTitle("Number of pedestrian crossings per meter");

                    graph.getViewport().setMinX(p[0].value);
                    graph.getViewport().setMaxX(p[p.length - 1].value);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getGridLabelRenderer().setHighlightZeroLines(false);
                }
            };
            mHandler.get_factor_time_interval_history("num_crossings_ped", _fromDateTime, _toDateTime, getStopFactorTimeIntervalListener);
        }



        return root;
    }
}

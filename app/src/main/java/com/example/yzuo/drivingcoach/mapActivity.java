package com.example.yzuo.drivingcoach;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.GpsStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mapActivity extends AppCompatActivity implements OnMapReadyCallback,  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Marker mCurrentLocationMarker;

    private RadioGroup mapRadioGroup;
    private RadioButton Route, Driving;

    private ImageButton savedTrips;

    private ImageButton gpsButton;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = "TEST_LOG_TAG";

    private static final int SELECT_INTERVAL_REQUEST_CODE = 1;

    private int trackRouteDrivingPressedCondition = 1;//1 for route ,2 for driving

    //private Marker mCurrentLocationMarker;

    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<Marker> route_markers = new ArrayList<>();
    private ArrayList<badBehavior> bad_driving_markers = new ArrayList<>();

    private ArrayList<LatLng> route_markers_traffic_light = new ArrayList<>();
    private ArrayList<LatLng> route_markers_ped_crossing = new ArrayList<>();

    private boolean FIRST_TIME = true;
    private boolean IS_ROUTE_CHECKED = true;

    private static final float ZOOM_LEVEL = 14;

    private static final String ROUTE_MARKER_TYPE_TRAFFIC_LIGHT = "traffic_light";
    private static final String ROUTE_MARKER_TYPE_PED_CROSSING = "ped_crossing";

    //By default should be latest trip in lastweek radio group
    private String option_chosen = "lastweek";
    private String tripID_chosen = "-1";
    private String segmentStartTime_chosen = "-1";

    private ImageButton weather;
    private ImageButton slippery;
    private ImageButton snow;
    private ImageButton grain;
    private ImageButton sun;
    private ImageButton clouds;
    private ImageButton sun_with_clouds;
    //Store the toast message
    private String weatherToast = "";
    /*
    * onCreate()
    * First checked the availability of google play service, which provides the google map and location service.
    * ---that are checkPlayService() and buildGoogleApiClient();
    *
    * Then initMap() using fragment to load a map, and add buttons on the layout of map.
    *
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        weather = (ImageButton) findViewById(R.id.weather);
        slippery = (ImageButton) findViewById(R.id.slippery);
        snow = (ImageButton) findViewById(R.id.snow);
        grain = (ImageButton) findViewById(R.id.grain);
        sun = (ImageButton) findViewById(R.id.sun);
        clouds = (ImageButton) findViewById(R.id.clouds);
        sun_with_clouds = (ImageButton) findViewById(R.id.sun_with_clouds);

        initWeatherButtonListeners();

        if(checkPlayService()){
            buildGoogleApiClient();
        }

        initMap();

        Route.setChecked(true);

        //listener of "Route" and "Driving"
        mapRadioGroup.setOnCheckedChangeListener(new checkListener());



        detectGPSCondition();

        //drawTripsPolyline(polyline_list);

        setSavedTripsButtonListener();

    }

    private void initWeatherButtonListeners() {
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
        slippery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
        snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
        grain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
        clouds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
        sun_with_clouds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), weatherToast, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSavedTripsButtonListener() {
        savedTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mapActivity.this,
                        savedTrips.class);
                //startActivity(myIntent);
                myIntent.putExtra("option", option_chosen);
                myIntent.putExtra("tripID", tripID_chosen);
                startActivityForResult(myIntent, SELECT_INTERVAL_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_INTERVAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                option_chosen = data.getStringExtra("option");

                tripID_chosen = data.getStringExtra("tripID");

                segmentStartTime_chosen = data.getStringExtra("segmentStartTime");

                //Get geom data and draw polyline of a trip
                commHandler mHandler = new commHandler(getApplicationContext());
                commHandler.onResponseListener getTripStatsListener = new commHandler.onResponseListener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //We will construct a Map with necessary key-values
                        Map stats = parseStatsString(s);
                        //Decide weather and slippery icons
                        String friction3_str = (String) stats.get("friction3");//weather:"@drawable/ic_stat_notify_weather_rainy_icon", slippery:ic_stat_notify_condition_slippery_icon
                        String roadsurfacetemperature1_str = (String) stats.get("roadsurfacetemperature1");
                        String precipitationintensity_str = (String) stats.get("precipitationintensity");
                        String precipitationtype_str = (String) stats.get("precipitationtype");
                        String roadsurfaceconditions1_str = (String) stats.get("roadsurfaceconditions1");
                        String airtemperature1_str = (String) stats.get("airtemperature1");
                        String humidity_str = (String) stats.get("humidity");
                        weatherToast = "";
                        if (airtemperature1_str.equals("null")){
                            weatherToast += "airtemperature: " + "null" + "\n";
                        }
                        else {
                            weatherToast += "airtemperature: " + String.format("%.2f", Double.parseDouble(airtemperature1_str)) + "\n";
                        }
                        if (humidity_str.equals("null")){
                            weatherToast += "humidity: " + "null" + "\n";
                        }
                        else {
                            weatherToast += "humidity: " + String.format("%.2f", Double.parseDouble(humidity_str)) + "\n";
                        }
                        if (friction3_str.equals("null")){
                            weatherToast += "friction: " + "null" + "\n";
                        }
                        else {
                            weatherToast += "friction: " + String.format("%.2f", Double.parseDouble(friction3_str));
                        }
                        //Slipperiness
                        if (decideSlipperiness(friction3_str, roadsurfacetemperature1_str, precipitationintensity_str, roadsurfaceconditions1_str) == 0) {
                            slippery.setBackgroundResource(0);
                        }
                        else if(decideSlipperiness(friction3_str, roadsurfacetemperature1_str, precipitationintensity_str, roadsurfaceconditions1_str) == 1) {
                            slippery.setBackgroundResource(R.drawable.ic_stat_notify_weather_slippery_icon);
                        }
                        else if(decideSlipperiness(friction3_str, roadsurfacetemperature1_str, precipitationintensity_str, roadsurfaceconditions1_str) == 2){
                            slippery.setBackgroundResource(R.drawable.ic_stat_notify_weather_very_slippery_icon);
                        }

                        //Rains, 0 if no rains, 1 if snow fall, 2 if very heavy rain, 3 if moderate rain, 4 if light rain, 5 if moderate  rain
                        switch(decideRains(airtemperature1_str,precipitationintensity_str, precipitationtype_str)){
                            case 0:
                                weather.setBackgroundResource(0);
                                break;
                            case 1:
                                weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_snow_fall_icon);
                                break;
                            case 2:
                                weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_very_heavy_rain_icon);
                                break;
                            case 3:
                                weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_moderate_rain_icon);
                                break;
                            case 4:
                                weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_light_rain_icon);
                                break;
                            case 5:
                                weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_moderate_rain_icon);
                                break;
                        }

                        //Snow, 0 if no snow, 1 if snow
                        if(decideSnow(precipitationtype_str) == 0){
                            snow.setBackgroundResource(0);
                        }
                        else {
                            snow.setBackgroundResource(R.drawable.ic_stat_notify_weather_snow_fall_icon);
                        }
                        //Grain,  0 if no grain, 1 if grain
                        if(decideGrain(precipitationtype_str) == 0){
                            grain.setBackgroundResource(0);
                        }
                        else{
                            grain.setBackgroundResource(R.drawable.ic_stat_notify_weather_grain_icon);
                        }
                        //Sun, 0 if nothing but sun_with_clouds, 1 if sun, 2 if clouds
                        switch(decideSun(precipitationtype_str, humidity_str)) {
                            case 0:
                                sun_with_clouds.setBackgroundResource(R.drawable.ic_stat_notify_weather_partly_cloudy_icon);
                                sun.setBackgroundResource(0);
                                clouds.setBackgroundResource(0);
                                break;
                            case 1:
                                sun_with_clouds.setBackgroundResource(0);
                                sun.setBackgroundResource(R.drawable.ic_stat_notify_weather_sunny_icon);
                                clouds.setBackgroundResource(0);
                                break;
                            case 2:
                                sun_with_clouds.setBackgroundResource(0);
                                sun.setBackgroundResource(0);
                                clouds.setBackgroundResource(R.drawable.ic_stat_notify_weather_cloudy_icon);
                                break;
                        }
                    }
                };

                commHandler.onResponseListener getTripRouteGeometryListener = new commHandler.onResponseListener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray a) {
                        //Construct an array of coords and draw it using my drawTripsPolyline function
                        for(int i = 0; i < a.length(); i++) {
                            try {
                                JSONArray coords_json = a.getJSONObject(i).getJSONArray("coordinates");
                                ArrayList<LatLng> coords = new ArrayList<>();
                                LatLng last_latlng = null;
                                for(int j = 0; j < coords_json.length(); j++) {
                                    coords.add(new LatLng(coords_json.getJSONObject(j).getDouble("x"), coords_json.getJSONObject(j).getDouble("y")));
                                    if(j == coords_json.length() - 1) {
                                        last_latlng = new LatLng(coords_json.getJSONObject(j).getDouble("x"), coords_json.getJSONObject(j).getDouble("y"));
                                    }
                                }
                                //Move camera to last latln added
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(last_latlng, ZOOM_LEVEL));
                                drawTripsPolyline(coords);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                //Almost the same as the listener written in drawLastTrip()
                commHandler.onResponseListener getRouteMarkersListener = new commHandler.onResponseListener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray a) {
                        //empty the latlng and markers list first
                        route_markers_traffic_light.clear();
                        route_markers_ped_crossing.clear();
                        route_markers.clear();

                        for(int i = 0; i < a.length(); i++) {
                            //Draw specific marker according to different types
                            try {
                                String type = a.getJSONObject(i).getString("type");
                                String latlng_string = a.getJSONObject(i).getString("coordinates");
                                LatLng latlng = parseStringToLatLng(latlng_string);
                                if(type.equals(ROUTE_MARKER_TYPE_TRAFFIC_LIGHT)) {
                                    route_markers_traffic_light.add(latlng);
                                }
                                else if(type.equals(ROUTE_MARKER_TYPE_PED_CROSSING)) {
                                    route_markers_ped_crossing.add(latlng);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //Draw only if route button is checked
                        if(IS_ROUTE_CHECKED == true) {
                            if(route_markers_traffic_light.size() > 0) {
                                drawRouteCondition(route_markers_traffic_light, ROUTE_MARKER_TYPE_TRAFFIC_LIGHT);
                            }
                            if(route_markers_ped_crossing.size() > 0) {
                                drawRouteCondition(route_markers_ped_crossing, ROUTE_MARKER_TYPE_PED_CROSSING);
                            }
                        }
                    }
                };
                commHandler.onResponseListener getBadDrivingMarkersListener = new commHandler.onResponseListener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray a) {
                        //empty the markers first
                        bad_driving_markers.clear();

                        for(int i = 0; i < a.length(); i++) {
                            //Draw specific marker according to different types
                            try {
                                String type = a.getJSONObject(i).getString("type");
                                String value = a.getJSONObject(i).getString("value");
                                String latlng_string = a.getJSONObject(i).getString("coordinates");
                                String point_time = a.getJSONObject(i).getString("point_time");
                                LatLng latlng = parseStringToLatLng(latlng_string);
                                MarkerOptions options = createBadDrivingMarker(latlng);
                                bad_driving_markers.add(new badBehavior(options, latlng, type, value, point_time));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //Draw only if driving button is checked
                        if(IS_ROUTE_CHECKED == false) {
                            for(badBehavior b: bad_driving_markers) {
                                b.addMarker(mMap);
                            }
                        }
                    }
                };
                //Clear previous poly lines first
                for(Polyline p: polylines) {
                    p.remove();
                }
                //Clear route markers
                if(route_markers.size() > 0) {
                    removeMarkers(route_markers);
                }
                //Clear bad driving markers
                for (badBehavior b: bad_driving_markers) {
                    if(b.marker != null) {
                        b.marker.remove();
                    }
                }
                mHandler.get_trip_route_geometry(tripID_chosen, segmentStartTime_chosen, getTripRouteGeometryListener);
                mHandler.get_trip_route_markers(tripID_chosen, segmentStartTime_chosen, getRouteMarkersListener);
                mHandler.get_trip_bad_driving_markers(tripID_chosen, segmentStartTime_chosen, getBadDrivingMarkersListener);
                mHandler.get_trip_statistics(tripID_chosen, segmentStartTime_chosen, getTripStatsListener);
            }
        }
    }


    private  boolean checkPlayService(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else{
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    /*public void initCoordinatesList() {
        polyline_list = new ArrayList<LatLng>();
        polyline_list.add(new LatLng(65.058972, 25.472376));
        polyline_list.add(new LatLng(65.055801, 25.472279));
        polyline_list.add(new LatLng(65.055570, 25.469404));
        polyline_list.add(new LatLng(65.053828, 25.469345));
        polyline_list.add(new LatLng(65.053801, 25.470654));
        polyline_list.add(new LatLng(65.050932, 25.471491));

        test_route_list = new ArrayList<LatLng>();
        test_route_list.add(new LatLng(65.053828, 25.469345));

        test_driving_list = new ArrayList<LatLng>();
        test_driving_list.add(new LatLng(65.053828, 25.469345));
    }*/

   /* private void initMarkersList() {
        test_route_markers = new ArrayList<Marker>();
        test_driving_markers = new ArrayList<Marker>();
    }
*/
    private void initMap() {
        //add map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get all radio buttons
        mapRadioGroup = (RadioGroup) findViewById(R.id.radio_group_list_selector);

        //set all radio buttons
        Route = (RadioButton) findViewById(R.id.route);
        Driving = (RadioButton) findViewById(R.id.driving);

        //init mMap
        mMap = mapFragment.getMap();

        savedTrips= (ImageButton) findViewById(R.id.saved_trip);
    }

    //checkListener
    public class checkListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.route:
                    //Toast.makeText(getApplicationContext(), "pressed route", Toast.LENGTH_LONG).show();
                    trackRouteDrivingPressedCondition = 1;
                    IS_ROUTE_CHECKED = true;
                    drawRouteCondition(route_markers_traffic_light, ROUTE_MARKER_TYPE_TRAFFIC_LIGHT);
                    drawRouteCondition(route_markers_ped_crossing, ROUTE_MARKER_TYPE_PED_CROSSING);
                    //Remove markers from map
                    for(badBehavior b: bad_driving_markers) {
                        if(b.marker != null) {
                            b.marker.remove();
                        }
                    }
                    break;

                case R.id.driving:
                    //Toast.makeText(getApplicationContext(), "press driving", Toast.LENGTH_LONG).show();
                    trackRouteDrivingPressedCondition = 2;
                    IS_ROUTE_CHECKED = false;
                    //Draw markers on the map
                    for(badBehavior b: bad_driving_markers) {
                        b.addMarker(mMap);
                    }
                    removeMarkers(route_markers);
                    break;
            }
        }
    }



    private void detectGPSCondition() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(getApplicationContext(), "GPS has already been enabled", Toast.LENGTH_SHORT).show();
            gpsButton = (ImageButton) findViewById(R.id.gps);
            gpsButton.setImageResource(R.drawable.btn_stat_notify_gps_on_icon);
            gpsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(getApplicationContext(), "GPS not enabled,  you can enable it", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "GPS has already been enabled", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            //Toast.makeText(getApplicationContext(), "GPS has already been disabled", Toast.LENGTH_SHORT).show();
            gpsButton = (ImageButton) findViewById(R.id.gps);
            gpsButton.setImageResource(R.drawable.btn_stat_notify_gps_off_icon);
            gpsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(getApplicationContext(), "GPS not enabled,  you can enable it", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "GPS has already been enabled", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    //To parse String like: "POINT(25.5058977829841    64.9271666560236)"  Remember lat is the latter one
    private LatLng parseStringToLatLng(String str) {
        String[] str2 = str.split("POINT\\(");
        //str2 should be only two, first is empty and latter one is what we need
        String[] str3 = str2[1].split("\\)");
        String[] parts = str3[0].split(" ");
        //There are multiple empty spaces in the middle
        double lat = Double.parseDouble(parts[parts.length - 1]);
        double lng = Double.parseDouble(parts[0]);
        return new LatLng(lat, lng);
    }

    //To parse String into a Map object with several key-value pairs
    private Map parseStatsString(String str) {
        Map ret = new HashMap<String, String>();

        int friction3_pos = 0;
        int airtemperature1_pos = 0;
        int roadsurfacetemperature1_pos = 0;
        int precipitationintensity_pos = 0;
        int precipitationtype_pos = 0;
        int roadsurfaceconditions1_pos = 0;
        int humidity_pos = 0;

        boolean is_friction3 = false;
        boolean is_airtemperature1 = false;
        boolean is_roadsurfacetemperature1 = false;
        boolean is_precipitationintensity = false;
        boolean is_precipitationtype = false;
        boolean is_roadsurfaceconditions1 = false;
        boolean is_humidity = false;

        String[] str2 = str.split("\\],\\[");

        //factors array
        String[] str3 = str2[0].split("\\[");

        //Parse factors string
        String[] str4 = str3[1].split(", ");
        int i = 0;
        for(String s: str4){
            if (s.equals("friction3")) {
                friction3_pos = i;
                is_friction3 = true;
            }
            else if (s.equals("airtemperature1")) {
                airtemperature1_pos = i;
                is_airtemperature1 = true;
            }
            else if (s.equals("roadsurfacetemperature1")) {
                roadsurfacetemperature1_pos = i;
                is_roadsurfacetemperature1 = true;
            }
            else if (s.equals("precipitationintensity")) {
                precipitationintensity_pos = i;
                is_precipitationintensity = true;
            }
            else if (s.equals("precipitationtype")) {
                precipitationtype_pos = i;
                is_precipitationtype = true;
            }
            else if (s.equals("roadsurfaceconditions1")) {
                roadsurfaceconditions1_pos = i;
                is_roadsurfaceconditions1 = true;
            }
            else if (s.equals("humidity")) {
                humidity_pos = i;
                is_humidity = true;
            }
            i++;
        }

        //Get current trip data according to positions parsed above
        String[] str5 = str2[1].split(", ");

        ret.put("friction3", str5[friction3_pos]);
        ret.put("airtemperature1", str5[airtemperature1_pos]);
        ret.put("roadsurfacetemperature1", str5[roadsurfacetemperature1_pos]);
        ret.put("precipitationintensity", str5[precipitationintensity_pos]);
        ret.put("precipitationtype", str5[precipitationtype_pos]);
        ret.put("roadsurfaceconditions1", str5[roadsurfaceconditions1_pos]);
        ret.put("humidity", str5[humidity_pos]);
        return ret;
    }

    //Return 0 if no slipperiness, 1 if slippery, 2 if very slippery
    private int decideSlipperiness(String friction3_str, String roadsurfacetemperature1_str, String precipitationintensity_str, String roadsurfaceconditions1_str) {
        if (!friction3_str.equals("null")) {
            Double friction3 = Double.parseDouble(friction3_str);
            if (friction3 < 0.15) {
                return 2;
            }
            else if (friction3 >= 0.15 && friction3 < 0.3) {
                return 1;
            }
        }
        if(!roadsurfacetemperature1_str.equals("null") && !precipitationintensity_str.equals("null")) {
            Double roadsurfacetemperature1 = Double.parseDouble(roadsurfacetemperature1_str);
            Double precipitationintensity = Double.parseDouble(precipitationintensity_str);
            if(roadsurfacetemperature1 < -6 && precipitationintensity > 0.2) {
                return 1;
            }
        }
        if(!roadsurfaceconditions1_str.equals("null")) {
            Double roadsurfaceconditions1 = Double.parseDouble(roadsurfaceconditions1_str);
            if (roadsurfaceconditions1 == 7) {
                return 1;
            }
        }
        return 0;
    }

    //Return 0 if no rains, 1 if snow fall, 2 if very heavy rain, 3 if moderate rain, 4 if rain, 5 if light rain
    private int decideRains(String airtemperature1_str, String precipitationintensity_str, String precipitationtype_str) {
        if(!airtemperature1_str.equals("null") && !precipitationintensity_str.equals("null")) {
            Double airtemperature1 = Double.parseDouble(airtemperature1_str);
            Double precipitationintensity = Double.parseDouble(precipitationintensity_str);
            if (airtemperature1 < 0 && precipitationintensity > 0.6) {
                return 1;
            }
        }
        if(!precipitationtype_str.equals("null") && !precipitationintensity_str.equals("null")) {
            Double precipitationtype = Double.parseDouble(precipitationtype_str);
            Double precipitationintensity = Double.parseDouble(precipitationintensity_str);
            if (precipitationtype == 10) {
                if (precipitationintensity >= 5) {
                    return 2;
                }
                else if (precipitationintensity < 5 && precipitationintensity >=2) {
                    return 3;
                }
                else if (precipitationintensity < 2) {
                    return 4;
                }
            }
            else if(precipitationtype == 8 || precipitationtype == 9) {
                return 5;
            }
        }
        return 0;
    }

    //Return 0 if no snow, 1 if snow
    private int decideSnow(String precipitationtype_str) {
        if(!precipitationtype_str.equals("null")) {
            Double precipitationtype = Double.parseDouble(precipitationtype_str);
            if(precipitationtype == 11 || precipitationtype == 12
                    || precipitationtype == 13 || precipitationtype == 18
                    || precipitationtype == 19) {
                return 1;
            }
        }
        return 0;
    }

    //Return 0 if no grain, 1 if grain
    private int decideGrain(String precipitationtype_str) {
        if(!precipitationtype_str.equals("null")) {
            Double precipitationtype = Double.parseDouble(precipitationtype_str);
            if(precipitationtype >= 14 && precipitationtype <= 17) {
                return 1;
            }
        }
        return 0;
    }

    //Sun, 0 if nothing but sun_with_clouds, 1 if sun, 2 if clouds
    private int decideSun(String precipitationtype_str, String humidity_str) {
        if(!precipitationtype_str.equals("null") && !humidity_str.equals("null")) {
            Double precipitationtype = Double.parseDouble(precipitationtype_str);
            Double humidity = Double.parseDouble(humidity_str);
            if(precipitationtype == 7) {
                if(humidity < 0.97) {
                    return 1;
                }
                else {
                    return 2;
                }
            }
        }
        return 0;
    }

    private void drawLastTrip() {
        final commHandler mHandler = new commHandler(this);
        final String[] lastTripId = new String[1];
        final String[] segmentStartTime = new String[1];

        final commHandler.onResponseListener getTripStatsListener = new commHandler.onResponseListener<String>() {
            public void onResponse(String str) {
                //We will construct a Map with necessary key-values
                Map stats = parseStatsString(str);
                //Decide weather and slippery icons
                String friction3_str = (String) stats.get("friction3");//weather:"@drawable/ic_stat_notify_weather_rainy_icon", slippery:ic_stat_notify_condition_slippery_icon
                String roadsurfacetemperature1_str = (String) stats.get("roadsurfacetemperature1");
                String precipitationintensity_str = (String) stats.get("precipitationintensity");
                String precipitationtype_str = (String) stats.get("precipitationtype");
                String roadsurfaceconditions1_str = (String) stats.get("roadsurfaceconditions1");
                String airtemperature1_str = (String) stats.get("airtemperature1");
                String humidity_str = (String) stats.get("humidity");
                weatherToast = "";
                if (airtemperature1_str.equals("null")){
                    weatherToast += "airtemperature: " + "null" + "\n";
                }
                else {
                    weatherToast += "airtemperature: " + String.format("%.2f", Double.parseDouble(airtemperature1_str)) + "\n";
                }
                if (humidity_str.equals("null")){
                    weatherToast += "humidity: " + "null" + "\n";
                }
                else {
                    weatherToast += "humidity: " + String.format("%.2f", Double.parseDouble(humidity_str)) + "\n";
                }
                if (friction3_str.equals("null")){
                    weatherToast += "friction: " + "null" + "\n";
                }
                else {
                    weatherToast += "friction: " + String.format("%.2f", Double.parseDouble(friction3_str));
                }
                //Slipperiness
                if (decideSlipperiness(friction3_str, roadsurfacetemperature1_str, precipitationintensity_str, roadsurfaceconditions1_str) == 0) {
                    slippery.setBackgroundResource(0);
                }
                else if(decideSlipperiness(friction3_str, roadsurfacetemperature1_str, precipitationintensity_str, roadsurfaceconditions1_str) == 1) {
                    slippery.setBackgroundResource(R.drawable.ic_stat_notify_weather_slippery_icon);
                }
                else if(decideSlipperiness(friction3_str, roadsurfacetemperature1_str, precipitationintensity_str, roadsurfaceconditions1_str) == 2){
                    slippery.setBackgroundResource(R.drawable.ic_stat_notify_weather_very_slippery_icon);
                }

                //Rains, 0 if no rains, 1 if snow fall, 2 if very heavy rain, 3 if moderate rain, 4 if light rain, 5 if moderate  rain
                switch(decideRains(airtemperature1_str,precipitationintensity_str, precipitationtype_str)){
                    case 0:
                        weather.setBackgroundResource(0);
                        break;
                    case 1:
                        weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_snow_fall_icon);
                        break;
                    case 2:
                        weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_very_heavy_rain_icon);
                        break;
                    case 3:
                        weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_moderate_rain_icon);
                        break;
                    case 4:
                        weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_light_rain_icon);
                        break;
                    case 5:
                        weather.setBackgroundResource(R.drawable.ic_stat_notify_weather_moderate_rain_icon);
                        break;
                }

                //Snow, 0 if no snow, 1 if snow
                if(decideSnow(precipitationtype_str) == 0){
                    snow.setBackgroundResource(0);
                }
                else {
                    snow.setBackgroundResource(R.drawable.ic_stat_notify_weather_snow_fall_icon);
                }
                //Grain,  0 if no grain, 1 if grain
                if(decideGrain(precipitationtype_str) == 0){
                    grain.setBackgroundResource(0);
                }
                else{
                    grain.setBackgroundResource(R.drawable.ic_stat_notify_weather_grain_icon);
                }
                //Sun, 0 if nothing but sun_with_clouds, 1 if sun, 2 if clouds
                switch(decideSun(precipitationtype_str, humidity_str)) {
                    case 0:
                        sun_with_clouds.setBackgroundResource(R.drawable.ic_stat_notify_weather_partly_cloudy_icon);
                        sun.setBackgroundResource(0);
                        clouds.setBackgroundResource(0);
                        break;
                    case 1:
                        sun_with_clouds.setBackgroundResource(0);
                        sun.setBackgroundResource(R.drawable.ic_stat_notify_weather_sunny_icon);
                        clouds.setBackgroundResource(0);
                        break;
                    case 2:
                        sun_with_clouds.setBackgroundResource(0);
                        sun.setBackgroundResource(0);
                        clouds.setBackgroundResource(R.drawable.ic_stat_notify_weather_cloudy_icon);
                        break;
                }
            }
        };

        final commHandler.onResponseListener getRouteMarkersListener = new commHandler.onResponseListener<JSONArray>() {

            @Override
            public void onResponse(JSONArray a) {
                for(int i = 0; i < a.length(); i++) {
                    //Draw specific marker according to different types
                    try {
                        String type = a.getJSONObject(i).getString("type");
                        String latlng_string = a.getJSONObject(i).getString("coordinates");
                        LatLng latlng = parseStringToLatLng(latlng_string);
                        if(type.equals(ROUTE_MARKER_TYPE_TRAFFIC_LIGHT)) {
                            //If you need to add later, remember to empty the array list first
                            route_markers_traffic_light.add(latlng);
                        }
                        else if(type.equals(ROUTE_MARKER_TYPE_PED_CROSSING)) {
                            route_markers_ped_crossing.add(latlng);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(route_markers_traffic_light.size() > 0) {
                    drawRouteCondition(route_markers_traffic_light, ROUTE_MARKER_TYPE_TRAFFIC_LIGHT);
                }
                if(route_markers_ped_crossing.size() > 0) {
                    drawRouteCondition(route_markers_ped_crossing, ROUTE_MARKER_TYPE_PED_CROSSING);
                }
            }
        };
        final commHandler.onResponseListener getBadDrivingMarkersListener = new commHandler.onResponseListener<JSONArray>() {

            @Override
            public void onResponse(JSONArray a) {
                for(int i = 0; i < a.length(); i++) {
                    //Draw specific marker according to different types
                    try {
                        String value = a.getJSONObject(i).getString("value");
                        String type = a.getJSONObject(i).getString("type");
                        String latlng_string = a.getJSONObject(i).getString("coordinates");
                        String point_time = a.getJSONObject(i).getString("point_time");
                        LatLng latlng = parseStringToLatLng(latlng_string);
                        MarkerOptions options = createBadDrivingMarker(latlng);
                        bad_driving_markers.add(new badBehavior(options, latlng, type, value, point_time));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //By default first time, route is checked, so won't draw these markers unless user clicks driving in the future
            }
        };
        final commHandler.onResponseListener getLastTripGeomListener = new commHandler.onResponseListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray a) {
                //Construct an array of coords and draw it using my drawTripsPolyline function
                for(int i = 0; i < a.length(); i++) {
                    try {
                        JSONArray coords_json = a.getJSONObject(i).getJSONArray("coordinates");
                        ArrayList<LatLng> coords = new ArrayList<>();
                        LatLng last_latlng = null;
                        for(int j = 0; j < coords_json.length(); j++) {
                            coords.add(new LatLng(coords_json.getJSONObject(j).getDouble("x"), coords_json.getJSONObject(j).getDouble("y")));
                            if(j == coords_json.length() - 1) {
                                last_latlng = new LatLng(coords_json.getJSONObject(j).getDouble("x"), coords_json.getJSONObject(j).getDouble("y"));
                            }
                        }
                        //Move camera to last latlng added
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(last_latlng, ZOOM_LEVEL));
                        drawTripsPolyline(coords);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        final commHandler.onResponseListener getLastTripSegmentStartTimeListener = new commHandler.onResponseListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray a) {
                //This will give us segmentStartTime
                try {
                    segmentStartTime[0] = a.getJSONObject(0).getString("segmentStartTime");
                    segmentStartTime_chosen = segmentStartTime[0];
                    //Get trip statistics
                    mHandler.get_trip_statistics(lastTripId[0], segmentStartTime[0], getTripStatsListener);
                    //Get route geometry
                    mHandler.get_trip_route_geometry(lastTripId[0], segmentStartTime[0], getLastTripGeomListener);
                    //Also get route markers and bad driving markers
                    mHandler.get_trip_route_markers(lastTripId[0], segmentStartTime[0], getRouteMarkersListener);
                    mHandler.get_trip_bad_driving_markers(lastTripId[0], segmentStartTime[0], getBadDrivingMarkersListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //Must be final, otherwise it cannot be called
        final commHandler.onResponseListener getLastTripIdListener = new commHandler.onResponseListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject o) {
                //This will give us the lastTripId
                try {
                    lastTripId[0] = o.getString("lastTripId");
                    tripID_chosen = lastTripId[0];
                    mHandler.get_last_trip_segmentStartTime(lastTripId[0], getLastTripSegmentStartTimeListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mHandler.get_last_trip_id(getLastTripIdListener);
    }

    private void drawTripsPolyline(ArrayList<LatLng> coord_list) {
        // Get back the mutable Polyline
        PolylineOptions options = new PolylineOptions().width(10).color(Color.rgb(39, 201, 162)).geodesic(true);
        options.addAll(coord_list);
        polylines.add(mMap.addPolyline(options));
    }


    private void drawRouteCondition(ArrayList<LatLng> coord_list, String type) {
        for (int i = 0; i < coord_list.size(); i++) {
            int resourceID = 0;
            if (type.equals("traffic_light")){
                resourceID = R.drawable.ic_stat_notify_traffic_light_icon;
            }
            else if (type.equals("ped_crossing")) {
                resourceID = R.drawable.ic_stat_notify_pedestrian_crossing_icon;
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(coord_list.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(resourceID)));
            route_markers.add(marker);
        }
    }

    private MarkerOptions createBadDrivingMarker(LatLng latlng) {
        MarkerOptions options = new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stat_notify_driving_behavior));

        return options;
    }


    private void removeMarkers(ArrayList<Marker> markers) {
        for(int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
    }

    private void showMeWhereIamWhenStarted(Location myLocation) {

        if(mMap == null) {
            Log.d(TAG, "Map object is not ready");
            return;
        }

        double latitude = myLocation.getLatitude();
        Log.d(TAG, "" + latitude);

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));

        // Zoom in the Google Map
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        mCurrentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("You are here!")
                .snippet("Consider yourself located"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume(){
        super.onResume();

        detectGPSCondition();

        //drawTripsPolyline(); maybe replaced by listener
        if (trackRouteDrivingPressedCondition == 1){
            //drawRouteCondition();
            Toast.makeText(getApplicationContext(), "route should be drawn", Toast.LENGTH_LONG);
        }
        else if(trackRouteDrivingPressedCondition == 2){
            //drawBadDrivingMakers();
            Toast.makeText(getApplicationContext(), "driving condition should be drawn", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null && FIRST_TIME == true) {
            //showMeWhereIamWhenStarted(mLastLocation);
            FIRST_TIME = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult i) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        /*// Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);*/

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Set listeners for route and driving markers
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker clicked_marker) {
                for (badBehavior b : bad_driving_markers) {
                    if (clicked_marker.equals(b.marker)) {
                        Intent myIntent = new Intent(mapActivity.this, drivingBehavior.class);
                        myIntent.putExtra("type", b.type);
                        myIntent.putExtra("value", b.value);
                        myIntent.putExtra("point_time", b.point_time);
                        myIntent.putExtra("tripID", tripID_chosen);
                        myIntent.putExtra("segmentStartTime", segmentStartTime_chosen);
                        startActivity(myIntent);
                        return true;
                    }
                }

                return false;
            }
        });

        //Set a listener for moving camera whenever clicks the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                //mMap.clear();
                //mCurrentLocationMarker.remove();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));

                // Placing a marker on the touched position
                //mCurrentLocationMarker = mMap.addMarker(markerOptions);
            }
        });

        //By default we get the last trip geometry, draw poly line and set route/driving markers accordingly
        drawLastTrip();
    }

}

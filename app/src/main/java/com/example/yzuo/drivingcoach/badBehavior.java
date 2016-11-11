package com.example.yzuo.drivingcoach;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by yzuo on 9.9.2015.
 */
public class badBehavior {
    public Marker marker;
    public LatLng latlng;
    public String type;
    public String value;
    public MarkerOptions options;
    public String point_time;


    public badBehavior(MarkerOptions options, LatLng latLng, String type, String value, String point_time){
        this.options = options;
        this.latlng = latLng;
        this.type = type;
        this.value = value;
        this.point_time = point_time;
    }

    public void addMarker(GoogleMap map){
        marker = map.addMarker(options);
    }
}

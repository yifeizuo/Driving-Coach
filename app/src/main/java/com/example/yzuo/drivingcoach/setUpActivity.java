package com.example.yzuo.drivingcoach;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class setUpActivity extends AppCompatActivity {

    private Button GPSHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        init();

        listenGPSStatus();
    }

    private void init() {
        GPSHandler = (Button) findViewById(R.id.gps_handler);
    }

    private void listenGPSStatus() {
        GPSHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    GPSHandler.setText("Enable");
                    Toast.makeText(getApplicationContext(), "GPS not enabled,  you can enable it", Toast.LENGTH_LONG).show();
                } else {
                    GPSHandler.setText("Disable");
                    Toast.makeText(getApplicationContext(), "GPS has already been enabled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPSHandler.setText("Disable");
            Toast.makeText(getApplicationContext(), "GPS has been enabled, you can disable it", Toast.LENGTH_LONG).show();
        } else {
            GPSHandler.setText("Enable");
            Toast.makeText(getApplicationContext(), "GPS has been disabled, you can enable it", Toast.LENGTH_LONG).show();
        }
    }

}

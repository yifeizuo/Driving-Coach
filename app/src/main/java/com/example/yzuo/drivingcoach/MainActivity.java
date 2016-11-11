package com.example.yzuo.drivingcoach;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends TabActivity {

    private TabHost tabHost;
    private RadioGroup main_radioGroup;
    private RadioButton tab_dashboard, tab_map, tab_statistics, tab_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        //get all radio buttons
        main_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);

        //set all radio buttons
        tab_dashboard = (RadioButton) findViewById(R.id.tab_dashboard);
        tab_map = (RadioButton) findViewById(R.id.tab_map);
        tab_statistics = (RadioButton) findViewById(R.id.tab_statistics);
        tab_setting = (RadioButton) findViewById(R.id.tab_setting);

        //set icon of dashboard as checked as default
        main_radioGroup.check(R.id.tab_dashboard);

        //add Tabs to TabWidget
        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tag1").setIndicator("").setContent(new Intent(this, dashboardActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tag2").setIndicator("").setContent(new Intent(this, mapActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tag3").setIndicator("").setContent(new Intent(this, statisticsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tag4").setIndicator("").setContent(new Intent(this, setUpActivity.class)));

        //listener of check
        main_radioGroup.setOnCheckedChangeListener(new checkListener());
    }


    //checkListener
    public class checkListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //setCurrentTab 通过标签索引设置当前显示的内容
            //setCurrentTabByTag 通过标签名设置当前显示的内容
            switch (checkedId) {
                case R.id.tab_map:
                    tabHost.setCurrentTabByTag("tag2");
                    break;
                case R.id.tab_statistics:
                    tabHost.setCurrentTabByTag("tag3");
                    break;
                case R.id.tab_setting:
                    tabHost.setCurrentTabByTag("tag4");
                    break;
                default: tabHost.setCurrentTabByTag("tag1");
                    break;
            }
        }
    }
}



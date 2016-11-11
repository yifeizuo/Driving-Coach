package com.example.yzuo.drivingcoach.statisticsActivitySwipe;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.widget.Toast;

/**
 * Created by Yifei on 8/10/2015.
 */
public class ViewPagerAdapterRoute extends FragmentStatePagerAdapter {
    private Context _context;
    public static int totalPage = 9;
    private String option;
    private String fromDateTime, toDateTime;

    public ViewPagerAdapterRoute(Context context, FragmentManager fm, String option) {
        super(fm);
        _context = context;
        this.option = option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setFromDateTime(String fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public void setToDateTime(String toDateTime) {
        this.toDateTime = toDateTime;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch(position){
            case 0:
                f =  option.equals("3") ? speed40percentage.newInstance(_context, option, fromDateTime, toDateTime) : speed40percentage.newInstance(_context, option);
                break;
            case 1:
                f =  option.equals("3") ? speed60percentage.newInstance(_context, option, fromDateTime, toDateTime) : speed60percentage.newInstance(_context, option);
                break;
            case 2:
                f =  option.equals("3") ? traffic_lights.newInstance(_context, option, fromDateTime, toDateTime) : traffic_lights.newInstance(_context, option);
                break;
            case 3:
                f =  option.equals("3") ? num_crossings.newInstance(_context, option, fromDateTime, toDateTime) : num_crossings.newInstance(_context, option);
                break;
            case 4:
                f =  option.equals("3") ? num_crossings_ped.newInstance(_context, option, fromDateTime, toDateTime) : num_crossings_ped.newInstance(_context, option);
                break;
            case 5:
                f =  option.equals("3") ? vtype1.newInstance(_context, option, fromDateTime, toDateTime) : vtype1.newInstance(_context, option);
                break;
            case 6:
                f =  option.equals("3") ? vtype2.newInstance(_context, option, fromDateTime, toDateTime) : vtype2.newInstance(_context, option);
                break;
            case 7:
                f =  option.equals("3") ? vtype3.newInstance(_context, option, fromDateTime, toDateTime) : vtype3.newInstance(_context, option);
                break;
            case 8:
                f =  option.equals("3") ? ftype3.newInstance(_context, option, fromDateTime, toDateTime) : ftype3.newInstance(_context, option);
                break;
        }
        return f;
    }
    @Override
    public int getCount() {
        return totalPage;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}


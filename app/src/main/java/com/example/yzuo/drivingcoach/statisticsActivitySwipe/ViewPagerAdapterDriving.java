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
public class ViewPagerAdapterDriving extends FragmentStatePagerAdapter {
    private Context _context;
    public static int totalPage = 7;
    private String option;
    private String fromDateTime, toDateTime;

    public ViewPagerAdapterDriving(Context context, FragmentManager fm, String option) {
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
                f =  option.equals("3") ? stop_factor.newInstance(_context, option, fromDateTime, toDateTime) : stop_factor.newInstance(_context, option);
                break;
            case 1:
                f =  option.equals("3") ? speed0_15.newInstance(_context, option, fromDateTime, toDateTime) : speed0_15.newInstance(_context, option);
                break;
            case 2:
                f =  option.equals("3") ? speed50_70.newInstance(_context, option, fromDateTime, toDateTime) : speed50_70.newInstance(_context, option);
                break;
            case 3:
                f =  option.equals("3") ? speed_osc.newInstance(_context, option, fromDateTime, toDateTime) : speed_osc.newInstance(_context, option);
                break;
            case 4:
                f =  option.equals("3") ? fuel.newInstance(_context, option, fromDateTime, toDateTime) : fuel.newInstance(_context, option);
                break;
            case 5:
                f =  option.equals("3") ? accel_high.newInstance(_context, option, fromDateTime, toDateTime) : accel_high.newInstance(_context, option);
                break;
            case 6:
                f =  option.equals("3") ? decel_av.newInstance(_context, option, fromDateTime, toDateTime) : decel_av.newInstance(_context, option);
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

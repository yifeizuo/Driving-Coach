package com.example.yzuo.drivingcoach;

/**
 * Created by Yifei on 9/14/2015.
 */
public class Pair implements Comparable<Pair>{
    public final int index;
    public final long value;

    public Pair(int index, long value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int compareTo(Pair another) {
        return Long.valueOf(this.value).compareTo(another.value);
    }
}

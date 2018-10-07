package com.binzosoft.audiotrackdemo.audio.wav.model;

import android.util.Log;

import java.util.ArrayList;

public class Lyric {

    private String TAG = getClass().getSimpleName();

    private ArrayList<LyricItem> items;
    private int index = -1;
    private int duration;

    public Lyric(int duration) {
        items = new ArrayList<>();
        this.duration = duration;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<LyricItem> getItems() {
        return items;
    }

    public boolean next() {
        int idx = index + 1;
        if (items.size() > 0 && idx >= 0 && idx <= items.size() - 2) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    public boolean previous() {
        int idx = index - 1;
        if (items.size() > 0 && idx >= 0 && idx <= items.size() - 2) {
            index--;
            return true;
        } else {
            return false;
        }
    }

    public void addItem(LyricItem item) {
        items.add(item);
    }

    public LyricItem getItem(int index) {
        return items.get(index);
    }

    public LyricItem getCurrentItem() {
        if (items.size() > 0 && index >= 0 && index < items.size()) {
            return items.get(index);
        } else {
            return null;
        }
    }

    public LyricItem getNextItem() {
        int idx = index + 1;
        if (items.size() > 0 && idx >= 0 && idx <= items.size() - 2) {
            return items.get(idx);
        } else {
            return null;
        }
    }

    public LyricItem getPreviousItem() {
        int idx = index - 1;
        if (items.size() > 0 && idx > 0 && idx < items.size()) {
            return items.get(idx);
        } else {
            return null;
        }
    }

    // 二分查找法定位当前播放时间点的歌词
    public void target(int msec) {
        Log.i(TAG, "msec:" + msec + ", duration:" + duration);
        if (msec < 0 || msec > duration) {  // 超出范围的时间无效
            index = -1;
            return;
        } else if (msec < items.get(0).getTimestamp()) {  // 小于第一个时间戳的情况
            index = -1;
            return;
        }
        int low = 0, high = items.size() - 1, mid = -1;
        while (low <= high) {
            mid = (low + high) / 2;
            Log.i(TAG, "low:" + low + ", high:" + high + ", mid:" + mid);
            int midTimestamp = items.get(mid).getTimestamp();
            int nextTimestamp = items.get(mid + 1).getTimestamp();
            Log.i(TAG, mid + "-midTimestamp:" + midTimestamp + "; " + (mid + 1) + "-nextTimestamp:" + nextTimestamp);
            if(msec >= midTimestamp && msec < nextTimestamp) {
                break;
            } else if(msec < midTimestamp) {
                high = mid - 1;
            } else if (msec >= nextTimestamp) {
                low = mid + 1;
            }
            if (low == high) {
                mid = low;
                break;
            }
        }
        index = mid;
        Log.i(TAG, "low:" + low + ", high:" + high + ", mid:" + mid);
    }

}

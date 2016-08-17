package com.mobile.swollestandroid.noteifi.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rudi on 8/17/2016.
 */
public class RouteDetail implements Serializable{

    private String summary;
    private String warnings;
    private ArrayList<String> duration = new ArrayList<>();
    private ArrayList<Long> lDuration = new ArrayList<>();
    private ArrayList<String> durationInTraffic = new ArrayList<>();
    private ArrayList<Long> lDurationInTraffic = new ArrayList<>();

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public ArrayList<String> getDuration() {
        return duration;
    }

    public void setDuration(ArrayList<String> duration) {
        this.duration = duration;
    }

    public ArrayList<Long> getlDuration() {
        return lDuration;
    }

    public void setlDuration(ArrayList<Long> lDuration) {
        this.lDuration = lDuration;
    }

    public ArrayList<String> getDurationInTraffic() {
        return durationInTraffic;
    }

    public void setDurationInTraffic(ArrayList<String> durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
    }

    public ArrayList<Long> getlDurationInTraffic() {
        return lDurationInTraffic;
    }

    public void setlDurationInTraffic(ArrayList<Long> lDurationInTraffic) {
        this.lDurationInTraffic = lDurationInTraffic;
    }

    public long getDurationTotal(){
        long durationTotal= 0;
        for(long num : lDuration){
            durationTotal += num;
        }
        return durationTotal;
    }

    public long getDurationInTrafficTotal(){
        long durationInTrafficTotal= 0;
        for(long num : lDurationInTraffic){
            durationInTrafficTotal += num;
        }
        return durationInTrafficTotal;
    }


}


package com.example.rahul.navigapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 1/16/2017.
 */

public class Step {

    @SerializedName("travel_mode")
    @Expose
    private String travel_mode;

    @SerializedName("polyline")
    @Expose
    private Polylines polyline;

    @SerializedName("end_location")
    @Expose
    private End_location end_location;


    @SerializedName("start_location")
    @Expose
    private Start_Location start_location;

    @SerializedName("html_instructions")
    @Expose
    private String html_instructions;

    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;

    public Polylines getPolyline() {
        return polyline;
    }

    public void setPolyline(Polylines polyline) {
        this.polyline = polyline;
    }

    public End_location getEnd_location() {
        return end_location;
    }

    public void setEnd_location(End_location end_location) {
        this.end_location = end_location;
    }

    public Start_Location getStart_location() {
        return start_location;
    }

    public void setStart_location(Start_Location start_location) {
        this.start_location = start_location;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTravel_mode() {
        return travel_mode;
    }

    public void setTravel_mode(String travel_mode) {
        this.travel_mode = travel_mode;
    }
}

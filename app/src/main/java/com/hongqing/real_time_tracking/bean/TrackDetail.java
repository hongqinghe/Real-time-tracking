package com.hongqing.real_time_tracking.bean;

/**
 * Created by 贺红清 on 2017/2/7.
 */

public class TrackDetail {
    private int id;
    private double lat;//纬度
    private double lng;//精度
    private Track track;

    public TrackDetail() {
    }

    public TrackDetail(double lat, double lng, Track track) {
        this.lat = lat;
        this.lng = lng;
        this.track = track;
    }

    public TrackDetail(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public TrackDetail(int id, double lat, double lng, Track track) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.track = track;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}

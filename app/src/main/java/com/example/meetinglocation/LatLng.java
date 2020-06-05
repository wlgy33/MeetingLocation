package com.example.meetinglocation;

import androidx.annotation.NonNull;

public class LatLng {
    private Double latitude;
    private Double longitude;
    public LatLng() {}

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}

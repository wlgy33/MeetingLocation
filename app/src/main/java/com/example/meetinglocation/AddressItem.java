package com.example.meetinglocation;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class AddressItem implements Serializable {
    String address;
    String name;
    String latlng;
    double latitude;
    double longitude;
    String time;
    String route;

    public AddressItem(String address, String name, String latlng, double latitude, double longitude) {
        this.address = address;
        this.name = name;
        this.latlng = latlng;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getLatLng() {return latlng;}

    public void setAdress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getTime(){return time;}

    public String getRoute(){ return route;}

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "AdressItem{" +
                "adress='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

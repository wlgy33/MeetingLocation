package com.example.meetinglocation;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.meetinglocation.LatLng;

import java.io.Serializable;

public class FriendsItem implements Serializable, Parcelable {
    String name;
    String address;
    LatLng latlng;

    public FriendsItem(String name, String address, LatLng latlng) {
        this.name = name;
        this.address = address;
        this.latlng = latlng;
    }

    public  FriendsItem() {

    }

    protected FriendsItem(Parcel in) {
        name = in.readString();
        address = in.readString();
        latlng = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeParcelable((Parcelable) latlng, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FriendsItem> CREATOR = new Creator<FriendsItem>() {
        @Override
        public FriendsItem createFromParcel(Parcel in) {
            return new FriendsItem(in);
        }

        @Override
        public FriendsItem[] newArray(int size) {
            return new FriendsItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAdress(String address) {
        this.address = address;
    }

    public LatLng getLatLng(){return latlng;}

    @Override
    public String toString() {
        return "FriendsItem{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

}


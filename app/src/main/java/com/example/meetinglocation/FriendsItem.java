package com.example.meetinglocation;

public class FriendsItem {
    String name;
    String address;

    public FriendsItem(String name, String address) {
        this.name = name;
        this.address = address;
    }

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

    @Override
    public String toString() {
        return "FriendsItem{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

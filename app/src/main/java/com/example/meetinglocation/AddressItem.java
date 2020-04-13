package com.example.meetinglocation;

public class AddressItem {
    String address;
    String name;

    public AddressItem(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAdress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

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

package com.example.yuzelli.bluetoothservice;

/**
 * Created by 51644 on 2017/8/1.
 */

public class MyBlueToothBean {
    private String name;
    private String address;

    public MyBlueToothBean(String name, String address) {
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

    public void setAddress(String address) {
        this.address = address;
    }
}

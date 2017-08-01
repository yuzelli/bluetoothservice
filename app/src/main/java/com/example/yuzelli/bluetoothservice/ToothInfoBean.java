package com.example.yuzelli.bluetoothservice;

import java.io.Serializable;

/**
 * Created by 51644 on 2017/8/1.
 */

public class ToothInfoBean implements Serializable {
    private String jaquan;
    private String ben;
    private String co2;
    private String co;
    private String so2;
    private String no;

    public ToothInfoBean(String jaquan, String ben, String co2, String co, String so2, String no) {
        this.jaquan = jaquan;
        this.ben = ben;
        this.co2 = co2;
        this.co = co;
        this.so2 = so2;
        this.no = no;
    }

    public String getJaquan() {
        return jaquan;
    }

    public void setJaquan(String jaquan) {
        this.jaquan = jaquan;
    }

    public String getBen() {
        return ben;
    }

    public void setBen(String ben) {
        this.ben = ben;
    }

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
        this.co2 = co2;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}

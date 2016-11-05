package com.letsgo.letsgo;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 20-08-2016.
 */
public class Settings implements Serializable {
    private String cityCode;
    String phone;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

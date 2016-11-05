package com.letsgo.letsgo;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 27-06-2016.
 */
public class User implements Serializable {
    String userid,name,phone,email,password;
    Boolean IsActive;
    Settings settings;
    CityBO city;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return IsActive;
    }

    public void setActive(Boolean active) {
        IsActive = active;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public CityBO getCity() {
        return city;
    }

    public void setCity(CityBO city) {
        this.city = city;
    }
}

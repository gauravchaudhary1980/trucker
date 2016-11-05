package com.letsgo.letsgo;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 20-08-2016.
 */
public class CityBO implements Serializable {
    String code,name,person,address,phone;
    boolean iscreate,isresume,isclose;
    public CityBO()
    {
        super();
    }
    public CityBO(String code,String name)
    {
        super();
        this.code=code;
        this.name=name;

    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean iscreate() {
        return iscreate;
    }

    public void setIscreate(boolean iscreate) {
        this.iscreate = iscreate;
    }

    public boolean isresume() {
        return isresume;
    }

    public void setIsresume(boolean isresume) {
        this.isresume = isresume;
    }

    public boolean isclose() {
        return isclose;
    }

    public void setIsclose(boolean isclose) {
        this.isclose = isclose;
    }

    //to display object as a string in spinner
    public String toString() {
        return name;
    }

}

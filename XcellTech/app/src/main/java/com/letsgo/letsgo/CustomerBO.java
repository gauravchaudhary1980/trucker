package com.letsgo.letsgo;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 20-08-2016.
 */
public class CustomerBO implements Serializable {
    String customerCode,name,address1,address2,city,phone1,phone2,email1,email2;
    public CustomerBO()
    {
        super();
    }
    public CustomerBO(String customerCode,String name)
    {
        super();
        this.customerCode=customerCode;
        this.name=name;

    }
    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    //to display object as a string in spinner
    public String toString() {
        return name + " (" + city + ")";
    }
}

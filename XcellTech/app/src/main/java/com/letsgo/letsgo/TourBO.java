package com.letsgo.letsgo;

import android.widget.AutoCompleteTextView;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 12-07-2016.
 */
public class TourBO  implements Serializable {

    ActivityBO activityBO;
    String customerCode,panNumber,quantity,vehicleNumber,driverNumber,grNumber,note;


    public ActivityBO getActivityBO() {
        return activityBO;
    }

    public void setActivityBO(ActivityBO activityBO) {
        this.activityBO = activityBO;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getGrNumber() {
        return grNumber;
    }

    public void setGrNumber(String grNumber) {
        this.grNumber = grNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

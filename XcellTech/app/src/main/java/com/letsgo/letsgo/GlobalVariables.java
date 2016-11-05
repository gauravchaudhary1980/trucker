package com.letsgo.letsgo;

import android.app.Application;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 05-07-2016.
 */
public class GlobalVariables extends Application implements Serializable {
    private static GlobalVariables instance;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static synchronized GlobalVariables getInstance(){
        if(instance==null){
            instance=new GlobalVariables();
        }
        return instance;
    }
}

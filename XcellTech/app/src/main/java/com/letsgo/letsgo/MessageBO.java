package com.letsgo.letsgo;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by gaurav.chaudhary on 10-07-2016.
 */
public class MessageBO implements Serializable {

    String messageType;
    String messageHeader;
    String messageDetail;
    String callBackActivity;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    public String getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail;
    }

    public String getCallBackActivity() {
        return callBackActivity;
    }

    public void setCallBackActivity(String callBackActivity) {
        this.callBackActivity = callBackActivity;
    }
}

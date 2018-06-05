
package com.application.onlineTestSeries.Home.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class subscriptionUserData extends RealmObject {

    @PrimaryKey
    @SerializedName("SUBSCRIBER_SUBSCRIPTION_DATE")
    @Expose
    private String sUBSCRIBERSUBSCRIPTIONDATE;
    @SerializedName("SUBSCRIBER_SUBSCRIPTION_STATUS")
    @Expose
    private String sUBSCRIBERSUBSCRIPTIONSTATUS;
    @SerializedName("SUBSCRIPTION_STATUS")
    @Expose
    private String sUBSCRIPTIONSTATUS;

    @SerializedName("isActive")
    @Expose
    private String activeStatus;

    public String getSUBSCRIBERSUBSCRIPTIONDATE() {
        return sUBSCRIBERSUBSCRIPTIONDATE;
    }

    public void setSUBSCRIBERSUBSCRIPTIONDATE(String sUBSCRIBERSUBSCRIPTIONDATE) {
        this.sUBSCRIBERSUBSCRIPTIONDATE = sUBSCRIBERSUBSCRIPTIONDATE;
    }

    public String getSUBSCRIBERSUBSCRIPTIONSTATUS() {
        return sUBSCRIBERSUBSCRIPTIONSTATUS;
    }

    public void setSUBSCRIBERSUBSCRIPTIONSTATUS(String sUBSCRIBERSUBSCRIPTIONSTATUS) {
        this.sUBSCRIBERSUBSCRIPTIONSTATUS = sUBSCRIBERSUBSCRIPTIONSTATUS;
    }

    public String getSUBSCRIPTIONSTATUS() {
        return sUBSCRIPTIONSTATUS;
    }

    public void setSUBSCRIPTIONSTATUS(String sUBSCRIPTIONSTATUS) {
        this.sUBSCRIPTIONSTATUS = sUBSCRIPTIONSTATUS;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }
}


package com.application.onlineTestSeries.Subscription_package.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionData {

    @SerializedName("SUBSCRIPTION_ID")
    @Expose
    private String sUBSCRIPTIONID;
    @SerializedName("SUBSCRIPTION_NAME")
    @Expose
    private String sUBSCRIPTIONNAME;
    @SerializedName("SUBSCRIPTION_VALIDITY")
    @Expose
    private String sUBSCRIPTIONVALIDITY;
    @SerializedName("SUBSCRIPTION_PRICE")
    @Expose
    private String sUBSCRIPTIONPRICE;
    @SerializedName("SUBSCRIPTION_PERIOD")
    @Expose
    private String sUBSCRIPTIONPERIOD;
    @SerializedName("SUBSCRIPTION_END_DATE")
    @Expose
    private Object sUBSCRIPTIONENDDATE;
    @SerializedName("SUBSCRIPTION_STATUS")
    @Expose
    private Object sUBSCRIPTIONSTATUS;

    @SerializedName("SUBSCRIPTION_DISCOUNT")
    @Expose
    private String SUBSCRIPTION_DISCOUNT;


    private int color;

    private boolean active;

    public String getSUBSCRIPTIONID() {
        return sUBSCRIPTIONID;
    }

    public void setSUBSCRIPTIONID(String sUBSCRIPTIONID) {
        this.sUBSCRIPTIONID = sUBSCRIPTIONID;
    }

    public String getSUBSCRIPTIONNAME() {
        return sUBSCRIPTIONNAME;
    }

    public void setSUBSCRIPTIONNAME(String sUBSCRIPTIONNAME) {
        this.sUBSCRIPTIONNAME = sUBSCRIPTIONNAME;
    }

    public String getSUBSCRIPTIONVALIDITY() {
        return sUBSCRIPTIONVALIDITY;
    }

    public void setSUBSCRIPTIONVALIDITY(String sUBSCRIPTIONVALIDITY) {
        this.sUBSCRIPTIONVALIDITY = sUBSCRIPTIONVALIDITY;
    }

    public String getSUBSCRIPTIONPRICE() {
        return sUBSCRIPTIONPRICE;
    }

    public void setSUBSCRIPTIONPRICE(String sUBSCRIPTIONPRICE) {
        this.sUBSCRIPTIONPRICE = sUBSCRIPTIONPRICE;
    }

    public String getSUBSCRIPTIONPERIOD() {
        return sUBSCRIPTIONPERIOD;
    }

    public void setSUBSCRIPTIONPERIOD(String sUBSCRIPTIONPERIOD) {
        this.sUBSCRIPTIONPERIOD = sUBSCRIPTIONPERIOD;
    }

    public Object getSUBSCRIPTIONENDDATE() {
        return sUBSCRIPTIONENDDATE;
    }

    public void setSUBSCRIPTIONENDDATE(Object sUBSCRIPTIONENDDATE) {
        this.sUBSCRIPTIONENDDATE = sUBSCRIPTIONENDDATE;
    }

    public Object getSUBSCRIPTIONSTATUS() {
        return sUBSCRIPTIONSTATUS;
    }

    public void setSUBSCRIPTIONSTATUS(Object sUBSCRIPTIONSTATUS) {
        this.sUBSCRIPTIONSTATUS = sUBSCRIPTIONSTATUS;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSUBSCRIPTION_DISCOUNT() {
        return SUBSCRIPTION_DISCOUNT;
    }

    public void setSUBSCRIPTION_DISCOUNT(String SUBSCRIPTION_DISCOUNT) {
        this.SUBSCRIPTION_DISCOUNT = SUBSCRIPTION_DISCOUNT;
    }
}

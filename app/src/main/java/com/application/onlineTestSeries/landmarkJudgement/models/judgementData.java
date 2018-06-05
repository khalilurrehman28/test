
package com.application.onlineTestSeries.landmarkJudgement.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class judgementData extends RealmObject{

    @PrimaryKey
    @SerializedName("SCLJ_ID")
    @Expose
    private String sCLJID;
    @SerializedName("SCLJ_TITLE")
    @Expose
    private String sCLJTITLE;
    @SerializedName("SCLJ_PARTY1")
    @Expose
    private String sCLJPARTY1;
    @SerializedName("SCLJ_PARTY2")
    @Expose
    private String sCLJPARTY2;
    @SerializedName("SCLJ_JUDGEMENT")
    @Expose
    private String sCLJJUDGEMENT;
    @SerializedName("SCLJ_DATE")
    @Expose
    private String sCLJDATE;
    @SerializedName("SCLJ_CASENO")
    @Expose
    private String sCLJCASENO;
    private int color;
    private int counter;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getSCLJID() {
        return sCLJID;
    }

    public void setSCLJID(String sCLJID) {
        this.sCLJID = sCLJID;
    }

    public String getSCLJTITLE() {
        return sCLJTITLE;
    }

    public void setSCLJTITLE(String sCLJTITLE) {
        this.sCLJTITLE = sCLJTITLE;
    }

    public String getSCLJPARTY1() {
        return sCLJPARTY1;
    }

    public void setSCLJPARTY1(String sCLJPARTY1) {
        this.sCLJPARTY1 = sCLJPARTY1;
    }

    public String getSCLJPARTY2() {
        return sCLJPARTY2;
    }

    public void setSCLJPARTY2(String sCLJPARTY2) {
        this.sCLJPARTY2 = sCLJPARTY2;
    }

    public String getSCLJJUDGEMENT() {
        return sCLJJUDGEMENT;
    }

    public void setSCLJJUDGEMENT(String sCLJJUDGEMENT) {
        this.sCLJJUDGEMENT = sCLJJUDGEMENT;
    }

    public String getSCLJDATE() {
        return sCLJDATE;
    }

    public void setSCLJDATE(String sCLJDATE) {
        this.sCLJDATE = sCLJDATE;
    }

    public String getSCLJCASENO() {
        return sCLJCASENO;
    }

    public void setSCLJCASENO(String sCLJCASENO) {
        this.sCLJCASENO = sCLJCASENO;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}

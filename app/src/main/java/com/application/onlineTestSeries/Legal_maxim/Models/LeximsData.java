
package com.application.onlineTestSeries.Legal_maxim.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LeximsData extends RealmObject{

    @PrimaryKey
    @SerializedName("LM_ID")
    @Expose
    private String lMID;
    @SerializedName("LM_TITLE")
    @Expose
    private String lMTITLE;
    @SerializedName("LM_DESC")
    @Expose
    private String lMDESC;
    @SerializedName("LM_DATE")
    @Expose
    private String lMDATE;
    @SerializedName("LM_DEL_STATUS")
    @Expose
    private String lMDELSTATUS;

    private int counter;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private int color;
    /**
     * No args constructor for use in serialization
     * 
     */
    public LeximsData() {
    }


    public String getLMID() {
        return lMID;
    }

    public void setLMID(String lMID) {
        this.lMID = lMID;
    }

    public String getLMTITLE() {
        return lMTITLE;
    }

    public void setLMTITLE(String lMTITLE) {
        this.lMTITLE = lMTITLE;
    }

    public String getLMDESC() {
        return lMDESC;
    }

    public void setLMDESC(String lMDESC) {
        this.lMDESC = lMDESC;
    }

    public String getLMDATE() {
        return lMDATE;
    }

    public void setLMDATE(String lMDATE) {
        this.lMDATE = lMDATE;
    }

    public String getLMDELSTATUS() {
        return lMDELSTATUS;
    }

    public void setLMDELSTATUS(String lMDELSTATUS) {
        this.lMDELSTATUS = lMDELSTATUS;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}

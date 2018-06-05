
package com.application.onlineTestSeries.Course.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CourseData extends RealmObject{

    @PrimaryKey
    @SerializedName("COURSE_ID")
    @Expose
    private String cOURSEID;
    @SerializedName("COURSE_TITLE")
    @Expose
    private String cOURSETITLE;
    @SerializedName("COURSE_ICON")
    @Expose
    private String cOURSEICON;
    @SerializedName("COURSE_DATE")
    @Expose
    private String cOURSEDATE;
    @SerializedName("COURSE_DEL_STATUS")
    @Expose
    private String cOURSEDELSTATUS;

    @SerializedName("COURSE_STATE_ID")
    @Expose
    private String cOURSESTATE;

    private int color;

    @SerializedName("TOTAL_CHAPTERS")
    @Expose
    private Integer TOTAL_CHAPTERS;


    /**
     * No args constructor for use in serialization
     * 
     */
    public CourseData() {
    }

    /**
     * 
     * @param cOURSETITLE
     * @param cOURSEICON
     * @param cOURSEID
     * @param cOURSEDATE
     * @param cOURSEDELSTATUS
     */
    public CourseData(String cOURSEID, String cOURSETITLE, String cOURSEICON, String cOURSEDATE, String cOURSEDELSTATUS) {
        super();
        this.cOURSEID = cOURSEID;
        this.cOURSETITLE = cOURSETITLE;
        this.cOURSEICON = cOURSEICON;
        this.cOURSEDATE = cOURSEDATE;
        this.cOURSEDELSTATUS = cOURSEDELSTATUS;
    }

    public String getCOURSEID() {
        return cOURSEID;
    }

    public void setCOURSEID(String cOURSEID) {
        this.cOURSEID = cOURSEID;
    }

    public String getCOURSETITLE() {
        return cOURSETITLE;
    }

    public void setCOURSETITLE(String cOURSETITLE) {
        this.cOURSETITLE = cOURSETITLE;
    }

    public String getCOURSEICON() {
        return cOURSEICON;
    }

    public void setCOURSEICON(String cOURSEICON) {
        this.cOURSEICON = cOURSEICON;
    }

    public String getCOURSEDATE() {
        return cOURSEDATE;
    }

    public void setCOURSEDATE(String cOURSEDATE) {
        this.cOURSEDATE = cOURSEDATE;
    }

    public String getCOURSEDELSTATUS() {
        return cOURSEDELSTATUS;
    }

    public void setCOURSEDELSTATUS(String cOURSEDELSTATUS) {
        this.cOURSEDELSTATUS = cOURSEDELSTATUS;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getcOURSESTATE() {
        return cOURSESTATE;
    }

    public void setcOURSESTATE(String cOURSESTATE) {
        this.cOURSESTATE = cOURSESTATE;
    }

    public Integer getTOTAL_CHAPTERS() {
        return TOTAL_CHAPTERS;
    }

    public void setTOTAL_CHAPTERS(Integer TOTAL_CHAPTERS) {
        this.TOTAL_CHAPTERS = TOTAL_CHAPTERS;
    }
}

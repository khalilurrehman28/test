
package com.application.onlineTestSeries.Chapters.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChapterData extends RealmObject{

    @PrimaryKey
    @SerializedName("CHAPTER_ID")
    @Expose
    private String cHAPTERID;
    @SerializedName("CHAPTER_TITLE")
    @Expose
    private String cHAPTERTITLE;
    @SerializedName("CHAPTER_SUBHEAD1")
    @Expose
    private String cHAPTERSUBHEAD1;
    @SerializedName("CHAPTER_SUBHEAD2")
    @Expose
    private String cHAPTERSUBHEAD2;
    @SerializedName("CHAPTER_SECTION_DESC")
    @Expose
    private String cHAPTERSECTIONDESC;
    @SerializedName("COURSE_ID_FK")
    @Expose
    private String cOURSEIDFK;
    @SerializedName("CHAPTER_DATE")
    @Expose
    private String cHAPTERDATE;
    private int colorCirlce;

  /*  public ChapterData(String cHAPTERID, String cHAPTERTITLE, String cHAPTERSUBHEAD1, String cHAPTERSUBHEAD2, String cHAPTERSECTIONDESC, String cOURSEIDFK, String cHAPTERDATE, int colorCirlce) {
        this.cHAPTERID = cHAPTERID;
        this.cHAPTERTITLE = cHAPTERTITLE;
        this.cHAPTERSUBHEAD1 = cHAPTERSUBHEAD1;
        this.cHAPTERSUBHEAD2 = cHAPTERSUBHEAD2;
        this.cHAPTERSECTIONDESC = cHAPTERSECTIONDESC;
        this.cOURSEIDFK = cOURSEIDFK;
        this.cHAPTERDATE = cHAPTERDATE;
        this.colorCirlce = colorCirlce;
    }*/


    public String getCHAPTERID() {
        return cHAPTERID;
    }

    public void setCHAPTERID(String cHAPTERID) {
        this.cHAPTERID = cHAPTERID;
    }

    public String getCHAPTERTITLE() {
        return cHAPTERTITLE;
    }

    public void setCHAPTERTITLE(String cHAPTERTITLE) {
        this.cHAPTERTITLE = cHAPTERTITLE;
    }

    public String getCHAPTERSUBHEAD1() {
        return cHAPTERSUBHEAD1;
    }

    public void setCHAPTERSUBHEAD1(String cHAPTERSUBHEAD1) {
        this.cHAPTERSUBHEAD1 = cHAPTERSUBHEAD1;
    }

    public String getCHAPTERSUBHEAD2() {
        return cHAPTERSUBHEAD2;
    }

    public void setCHAPTERSUBHEAD2(String cHAPTERSUBHEAD2) {
        this.cHAPTERSUBHEAD2 = cHAPTERSUBHEAD2;
    }

    public String getCHAPTERSECTIONDESC() {
        return cHAPTERSECTIONDESC;
    }

    public void setCHAPTERSECTIONDESC(String cHAPTERSECTIONDESC) {
        this.cHAPTERSECTIONDESC = cHAPTERSECTIONDESC;
    }

    public String getCOURSEIDFK() {
        return cOURSEIDFK;
    }

    public void setCOURSEIDFK(String cOURSEIDFK) {
        this.cOURSEIDFK = cOURSEIDFK;
    }

    public String getCHAPTERDATE() {
        return cHAPTERDATE;
    }

    public void setCHAPTERDATE(String cHAPTERDATE) {
        this.cHAPTERDATE = cHAPTERDATE;
    }

    public int getColorCirlce() {
        return colorCirlce;
    }

    public void setColorCirlce(int colorCirlce) {
        this.colorCirlce = colorCirlce;
    }
}

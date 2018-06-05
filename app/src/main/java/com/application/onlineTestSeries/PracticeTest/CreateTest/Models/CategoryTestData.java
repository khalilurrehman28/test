
package com.application.onlineTestSeries.PracticeTest.CreateTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryTestData {

    @SerializedName("CATEGORY_ID")
    @Expose
    private String cATEGORYID;
    @SerializedName("CATEGORY_TITLE")
    @Expose
    private String cATEGORYTITLE;
    @SerializedName("CATEGORY_DATE")
    @Expose
    private String cATEGORYDATE;
    @SerializedName("STATE_ID_FK")
    @Expose
    private String sTATEIDFK;
    @SerializedName("CATEGORY_DEL_STATUS")
    @Expose
    private String cATEGORYDELSTATUS;

    @SerializedName("QUESTION_COUNT")
    @Expose
    private Integer Qcount;


    private int colorCode;

    private int questionCount;

    public String getCATEGORYID() {
        return cATEGORYID;
    }

    public void setCATEGORYID(String cATEGORYID) {
        this.cATEGORYID = cATEGORYID;
    }

    public String getCATEGORYTITLE() {
        return cATEGORYTITLE;
    }

    public void setCATEGORYTITLE(String cATEGORYTITLE) {
        this.cATEGORYTITLE = cATEGORYTITLE;
    }

    public String getCATEGORYDATE() {
        return cATEGORYDATE;
    }

    public void setCATEGORYDATE(String cATEGORYDATE) {
        this.cATEGORYDATE = cATEGORYDATE;
    }

    public String getSTATEIDFK() {
        return sTATEIDFK;
    }

    public void setSTATEIDFK(String sTATEIDFK) {
        this.sTATEIDFK = sTATEIDFK;
    }

    public String getCATEGORYDELSTATUS() {
        return cATEGORYDELSTATUS;
    }

    public void setCATEGORYDELSTATUS(String cATEGORYDELSTATUS) {
        this.cATEGORYDELSTATUS = cATEGORYDELSTATUS;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getQcount() {
        return Qcount;
    }

    public void setQcount(Integer qcount) {
        Qcount = qcount;
    }
}

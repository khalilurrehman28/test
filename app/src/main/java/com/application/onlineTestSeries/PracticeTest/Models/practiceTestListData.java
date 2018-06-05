
package com.application.onlineTestSeries.PracticeTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class practiceTestListData {

    @SerializedName("TEST_ID")
    @Expose
    private String tESTID;
    @SerializedName("TEST_NAME")
    @Expose
    private String tESTNAME;
    @SerializedName("TEST_TYPE")
    @Expose
    private String tESTTYPE;
    @SerializedName("TEST_INSTRUCTIONS")
    @Expose
    private String tESTINSTRUCTIONS;
    @SerializedName("TEST_DURATION")
    @Expose
    private String tESTDURATION;
    @SerializedName("TEST_QUESTIONS")
    @Expose
    private String tESTQUESTIONS;
    @SerializedName("TEST_SORT_ID")
    @Expose
    private String tESTSORTID;
    @SerializedName("TEST_DEL_STATUS")
    @Expose
    private String tESTDELSTATUS;
    @SerializedName("TEST_DATE")
    @Expose
    private String tESTDATE;

    @SerializedName("CREATED_BY")
    @Expose
    private String CREATED_BY;

    @SerializedName("TEST_MAX_QUESTION")
    @Expose
    private String TEST_MAX_QUESTION;

    @SerializedName("TEST_PURCHASE")
    @Expose
    private Boolean TEST_PURCHASE;

    @SerializedName("TEST_PRICE")
    @Expose
    private String TEST_PRICE;

    private int subscriptionFlag;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private int color;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

    public String getTESTID() {
        return tESTID;
    }

    public void setTESTID(String tESTID) {
        this.tESTID = tESTID;
    }

    public String getTESTNAME() {
        return tESTNAME;
    }

    public void setTESTNAME(String tESTNAME) {
        this.tESTNAME = tESTNAME;
    }

    public String getTESTTYPE() {
        return tESTTYPE;
    }

    public void setTESTTYPE(String tESTTYPE) {
        this.tESTTYPE = tESTTYPE;
    }

    public String getTESTINSTRUCTIONS() {
        return tESTINSTRUCTIONS;
    }

    public void setTESTINSTRUCTIONS(String tESTINSTRUCTIONS) {
        this.tESTINSTRUCTIONS = tESTINSTRUCTIONS;
    }

    public String getTESTDURATION() {
        return tESTDURATION;
    }

    public void setTESTDURATION(String tESTDURATION) {
        this.tESTDURATION = tESTDURATION;
    }

    public String getTESTQUESTIONS() {
        return tESTQUESTIONS;
    }

    public void setTESTQUESTIONS(String tESTQUESTIONS) {
        this.tESTQUESTIONS = tESTQUESTIONS;
    }

    public String getTESTSORTID() {
        return tESTSORTID;
    }

    public void setTESTSORTID(String tESTSORTID) {
        this.tESTSORTID = tESTSORTID;
    }

    public String getTESTDELSTATUS() {
        return tESTDELSTATUS;
    }

    public void setTESTDELSTATUS(String tESTDELSTATUS) {
        this.tESTDELSTATUS = tESTDELSTATUS;
    }

    public String getTESTDATE() {
        return tESTDATE;
    }

    public void setTESTDATE(String tESTDATE) {
        this.tESTDATE = tESTDATE;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public String getTEST_MAX_QUESTION() {
        return TEST_MAX_QUESTION;
    }

    public void setTEST_MAX_QUESTION(String TEST_MAX_QUESTION) {
        this.TEST_MAX_QUESTION = TEST_MAX_QUESTION;
    }

    public Boolean getTEST_PURCHASE() {
        return TEST_PURCHASE;
    }

    public void setTEST_PURCHASE(Boolean TEST_PURCHASE) {
        this.TEST_PURCHASE = TEST_PURCHASE;
    }

    public int getSubscriptionFlag() {
        return subscriptionFlag;
    }

    public void setSubscriptionFlag(int subscriptionFlag) {
        this.subscriptionFlag = subscriptionFlag;
    }

    public String getTEST_PRICE() {
        return TEST_PRICE;
    }

    public void setTEST_PRICE(String TEST_PRICE) {
        this.TEST_PRICE = TEST_PRICE;
    }
}

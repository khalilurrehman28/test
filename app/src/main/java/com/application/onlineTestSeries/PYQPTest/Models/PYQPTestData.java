
package com.application.onlineTestSeries.PYQPTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PYQPTestData {

    @SerializedName("PYQP_TEST_ID")
    @Expose
    private String pYQPTESTID;
    @SerializedName("PYQP_TEST_NAME")
    @Expose
    private String pYQPTESTNAME;
    @SerializedName("PYQP_STATE_ID_FK")
    @Expose
    private String pYQPSTATEIDFK;
    @SerializedName("PYQP_TEST_DESC")
    @Expose
    private String pYQPTESTDESC;
    @SerializedName("PYQP_TEST_QUESTIONS")
    @Expose
    private String pYQPTESTQUESTIONS;
    @SerializedName("PYQP_TEST_SORT_ID")
    @Expose
    private String pYQPTESTSORTID;
    @SerializedName("PYQP_TEST_TYPE")
    @Expose
    private String pYQPTESTTYPE;
    @SerializedName("PYQP_TEST_DURATION")
    @Expose
    private String pYQPTESTDURATION;
    @SerializedName("PYQP_TEST_DATE")
    @Expose
    private String pYQPTESTDATE;
    @SerializedName("PYQP_TEST_DEL_STATUS")
    @Expose
    private String pYQPTESTDELSTATUS;

    @SerializedName("CORRECT_ANSWER")
    @Expose
    private String CORRECT_ANSWER;

    @SerializedName("TOTAL_QUESTION")
    @Expose
    private String TOTAL_QUESTION;

    @SerializedName("ATTEMPTED_ANSWER")
    @Expose
    private String ATTEMPTED_ANSWER;

    @SerializedName("WRONG_ANSWER")
    @Expose
    private String WRONG_ANSWER;
    @SerializedName("CREATED_BY")
    @Expose
    private String CREATED_BY;

    @SerializedName("USER_STATUS")
    @Expose
    private String USER_STATUS;

    @SerializedName("purchaseStatus")
    @Expose
    private Boolean purchaseStatus;

    @SerializedName("PYQP_TEST_PRICE")
    @Expose
    private String testPrice;

    public String getCORRECT_ANSWER() {
        return CORRECT_ANSWER;
    }

    public void setCORRECT_ANSWER(String CORRECT_ANSWER) {
        this.CORRECT_ANSWER = CORRECT_ANSWER;
    }

    public String getTOTAL_QUESTION() {
        return TOTAL_QUESTION;
    }

    public void setTOTAL_QUESTION(String TOTAL_QUESTION) {
        this.TOTAL_QUESTION = TOTAL_QUESTION;
    }

    public String getATTEMPTED_ANSWER() {
        return ATTEMPTED_ANSWER;
    }

    public void setATTEMPTED_ANSWER(String ATTEMPTED_ANSWER) {
        this.ATTEMPTED_ANSWER = ATTEMPTED_ANSWER;
    }

    public String getWRONG_ANSWER() {
        return WRONG_ANSWER;
    }

    public void setWRONG_ANSWER(String WRONG_ANSWER) {
        this.WRONG_ANSWER = WRONG_ANSWER;
    }

    @SerializedName("STATUS")
    @Expose
    private boolean status;

    private int count;

    private int color;

    public String getPYQPTESTID() {
        return pYQPTESTID;
    }

    public void setPYQPTESTID(String pYQPTESTID) {
        this.pYQPTESTID = pYQPTESTID;
    }

    public String getPYQPTESTNAME() {
        return pYQPTESTNAME;
    }

    public void setPYQPTESTNAME(String pYQPTESTNAME) {
        this.pYQPTESTNAME = pYQPTESTNAME;
    }

    public String getPYQPSTATEIDFK() {
        return pYQPSTATEIDFK;
    }

    public void setPYQPSTATEIDFK(String pYQPSTATEIDFK) {
        this.pYQPSTATEIDFK = pYQPSTATEIDFK;
    }

    public String getPYQPTESTDESC() {
        return pYQPTESTDESC;
    }

    public void setPYQPTESTDESC(String pYQPTESTDESC) {
        this.pYQPTESTDESC = pYQPTESTDESC;
    }

    public String getPYQPTESTQUESTIONS() {
        return pYQPTESTQUESTIONS;
    }

    public void setPYQPTESTQUESTIONS(String pYQPTESTQUESTIONS) {
        this.pYQPTESTQUESTIONS = pYQPTESTQUESTIONS;
    }

    public String getPYQPTESTSORTID() {
        return pYQPTESTSORTID;
    }

    public void setPYQPTESTSORTID(String pYQPTESTSORTID) {
        this.pYQPTESTSORTID = pYQPTESTSORTID;
    }

    public String getPYQPTESTTYPE() {
        return pYQPTESTTYPE;
    }

    public void setPYQPTESTTYPE(String pYQPTESTTYPE) {
        this.pYQPTESTTYPE = pYQPTESTTYPE;
    }

    public String getPYQPTESTDURATION() {
        return pYQPTESTDURATION;
    }

    public void setPYQPTESTDURATION(String pYQPTESTDURATION) {
        this.pYQPTESTDURATION = pYQPTESTDURATION;
    }

    public String getPYQPTESTDATE() {
        return pYQPTESTDATE;
    }

    public void setPYQPTESTDATE(String pYQPTESTDATE) {
        this.pYQPTESTDATE = pYQPTESTDATE;
    }

    public String getPYQPTESTDELSTATUS() {
        return pYQPTESTDELSTATUS;
    }

    public void setPYQPTESTDELSTATUS(String pYQPTESTDELSTATUS) {
        this.pYQPTESTDELSTATUS = pYQPTESTDELSTATUS;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public String getUSER_STATUS() {
        return USER_STATUS;
    }

    public void setUSER_STATUS(String USER_STATUS) {
        this.USER_STATUS = USER_STATUS;
    }

    public Boolean getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(Boolean purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public String getTestPrice() {
        return testPrice;
    }

    public void setTestPrice(String testPrice) {
        this.testPrice = testPrice;
    }
}

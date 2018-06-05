
package com.application.onlineTestSeries.PYQPTest.Test.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.notStarted;

public class PYQP_Test_Data {

    @SerializedName("QUESTION_ID")
    @Expose
    private String qUESTIONID;
    @SerializedName("QUESTION_TITLE")
    @Expose
    private String qUESTIONTITLE;
    @SerializedName("QUESTION_OPTION1")
    @Expose
    private String qUESTIONOPTION1;
    @SerializedName("QUESTION_OPTION2")
    @Expose
    private String qUESTIONOPTION2;
    @SerializedName("QUESTION_OPTION3")
    @Expose
    private String qUESTIONOPTION3;
    @SerializedName("QUESTION_OPTION4")
    @Expose
    private String qUESTIONOPTION4;
    @SerializedName("QUESTION_OPTION5")
    @Expose
    private String qUESTIONOPTION5;
    @SerializedName("QUESTION_CORRECT_OPTION")
    @Expose
    private String qUESTIONCORRECTOPTION;
    @SerializedName("QUESTION_ANS_DESC")
    @Expose
    private String qUESTIONANSDESC;
    @SerializedName("QUESTION_SORT_ID")
    @Expose
    private String qUESTIONSORTID;
    @SerializedName("QUESTION_DEL_STATUS")
    @Expose
    private String qUESTIONDELSTATUS;
    @SerializedName("CATEGORY_ID_FK")
    @Expose
    private String cATEGORYIDFK;
    @SerializedName("QUESTION_DATE")
    @Expose
    private String qUESTIONDATE;

    private boolean isAttempted;
    private boolean isMarked;
    private int answerProvided;
    private int counter;
    private String testID;

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    public String getProcessStart() {
        return processStart;
    }

    public void setProcessStart(String processStart) {
        this.processStart = processStart;
    }

    private String processStart = notStarted;


    /**
     * 
     * @param qUESTIONDELSTATUS
     * @param qUESTIONTITLE
     * @param cATEGORYIDFK
     * @param qUESTIONOPTION3
     * @param qUESTIONOPTION4
     * @param qUESTIONANSDESC
     * @param qUESTIONOPTION5
     * @param qUESTIONOPTION1
     * @param qUESTIONSORTID
     * @param qUESTIONCORRECTOPTION
     * @param qUESTIONOPTION2
     * @param qUESTIONDATE
     * @param qUESTIONID
     */
    public PYQP_Test_Data(String qUESTIONID, String qUESTIONTITLE, String qUESTIONOPTION1, String qUESTIONOPTION2, String qUESTIONOPTION3, String qUESTIONOPTION4, String qUESTIONOPTION5, String qUESTIONCORRECTOPTION, String qUESTIONANSDESC, String qUESTIONSORTID, String qUESTIONDELSTATUS, String cATEGORYIDFK, String qUESTIONDATE,int counter,int answerProvided,boolean isAttempted,String processStart,String testID) {
        super();
        this.qUESTIONID = qUESTIONID;
        this.qUESTIONTITLE = qUESTIONTITLE;
        this.qUESTIONOPTION1 = qUESTIONOPTION1;
        this.qUESTIONOPTION2 = qUESTIONOPTION2;
        this.qUESTIONOPTION3 = qUESTIONOPTION3;
        this.qUESTIONOPTION4 = qUESTIONOPTION4;
        this.qUESTIONOPTION5 = qUESTIONOPTION5;
        this.qUESTIONCORRECTOPTION = qUESTIONCORRECTOPTION;
        this.qUESTIONANSDESC = qUESTIONANSDESC;
        this.qUESTIONSORTID = qUESTIONSORTID;
        this.qUESTIONDELSTATUS = qUESTIONDELSTATUS;
        this.cATEGORYIDFK = cATEGORYIDFK;
        this.qUESTIONDATE = qUESTIONDATE;
        this.answerProvided = answerProvided;
        this.isAttempted = isAttempted;
        this.processStart = processStart;
        this.counter = counter;
        this.setMarked(false);
        this.testID = testID;
    }

    public int getAnswerProvided() {
        return answerProvided;
    }

    public void setAnswerProvided(int answerProvided) {
        this.answerProvided = answerProvided;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public String getQUESTIONID() {
        return qUESTIONID;
    }

    public void setQUESTIONID(String qUESTIONID) {
        this.qUESTIONID = qUESTIONID;
    }

    public String getQUESTIONTITLE() {
        return qUESTIONTITLE;
    }

    public void setQUESTIONTITLE(String qUESTIONTITLE) {
        this.qUESTIONTITLE = qUESTIONTITLE;
    }

    public String getQUESTIONOPTION1() {
        return qUESTIONOPTION1;
    }

    public void setQUESTIONOPTION1(String qUESTIONOPTION1) {
        this.qUESTIONOPTION1 = qUESTIONOPTION1;
    }

    public String getQUESTIONOPTION2() {
        return qUESTIONOPTION2;
    }

    public void setQUESTIONOPTION2(String qUESTIONOPTION2) {
        this.qUESTIONOPTION2 = qUESTIONOPTION2;
    }

    public String getQUESTIONOPTION3() {
        return qUESTIONOPTION3;
    }

    public void setQUESTIONOPTION3(String qUESTIONOPTION3) {
        this.qUESTIONOPTION3 = qUESTIONOPTION3;
    }

    public String getQUESTIONOPTION4() {
        return qUESTIONOPTION4;
    }

    public void setQUESTIONOPTION4(String qUESTIONOPTION4) {
        this.qUESTIONOPTION4 = qUESTIONOPTION4;
    }

    public String getQUESTIONOPTION5() {
        return qUESTIONOPTION5;
    }

    public void setQUESTIONOPTION5(String qUESTIONOPTION5) {
        this.qUESTIONOPTION5 = qUESTIONOPTION5;
    }

    public String getQUESTIONCORRECTOPTION() {
        return qUESTIONCORRECTOPTION;
    }

    public void setQUESTIONCORRECTOPTION(String qUESTIONCORRECTOPTION) {
        this.qUESTIONCORRECTOPTION = qUESTIONCORRECTOPTION;
    }

    public String getQUESTIONANSDESC() {
        return qUESTIONANSDESC;
    }

    public void setQUESTIONANSDESC(String qUESTIONANSDESC) {
        this.qUESTIONANSDESC = qUESTIONANSDESC;
    }

    public String getQUESTIONSORTID() {
        return qUESTIONSORTID;
    }

    public void setQUESTIONSORTID(String qUESTIONSORTID) {
        this.qUESTIONSORTID = qUESTIONSORTID;
    }

    public String getQUESTIONDELSTATUS() {
        return qUESTIONDELSTATUS;
    }

    public void setQUESTIONDELSTATUS(String qUESTIONDELSTATUS) {
        this.qUESTIONDELSTATUS = qUESTIONDELSTATUS;
    }

    public String getCATEGORYIDFK() {
        return cATEGORYIDFK;
    }

    public void setCATEGORYIDFK(String cATEGORYIDFK) {
        this.cATEGORYIDFK = cATEGORYIDFK;
    }

    public String getQUESTIONDATE() {
        return qUESTIONDATE;
    }

    public void setQUESTIONDATE(String qUESTIONDATE) {
        this.qUESTIONDATE = qUESTIONDATE;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }
}

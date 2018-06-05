
package com.application.onlineTestSeries.PYQPTest.Test.TestResult.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class resultData {

    @SerializedName("PYQP_TEST_MARK_NEGATIVE")
    @Expose
    private String pYQPTESTMARKNEGATIVE;
    @SerializedName("RESULT_ID")
    @Expose
    private String rESULTID;
    @SerializedName("TEST_ID")
    @Expose
    private String tESTID;
    @SerializedName("USER_ID")
    @Expose
    private String uSERID;
    @SerializedName("CORRECT_ANSWER")
    @Expose
    private String cORRECTANSWER;
    @SerializedName("TOTAL_QUESTION")
    @Expose
    private String tOTALQUESTION;
    @SerializedName("ATTEMPTED_ANSWER")
    @Expose
    private String aTTEMPTEDANSWER;
    @SerializedName("WRONG_ANSWER")
    @Expose
    private String wRONGANSWER;
    @SerializedName("STATUS")
    @Expose
    private String sTATUS;
    @SerializedName("TIME_TAKEN")
    @Expose
    private String tIMETAKEN;
    @SerializedName("PYQP_TEST_NAME")
    @Expose
    private String pYQPTESTNAME;

    public String getPYQPTESTMARKNEGATIVE() {
        return pYQPTESTMARKNEGATIVE;
    }

    public void setPYQPTESTMARKNEGATIVE(String pYQPTESTMARKNEGATIVE) {
        this.pYQPTESTMARKNEGATIVE = pYQPTESTMARKNEGATIVE;
    }

    public String getRESULTID() {
        return rESULTID;
    }

    public void setRESULTID(String rESULTID) {
        this.rESULTID = rESULTID;
    }

    public String getTESTID() {
        return tESTID;
    }

    public void setTESTID(String tESTID) {
        this.tESTID = tESTID;
    }

    public String getUSERID() {
        return uSERID;
    }

    public void setUSERID(String uSERID) {
        this.uSERID = uSERID;
    }

    public String getCORRECTANSWER() {
        return cORRECTANSWER;
    }

    public void setCORRECTANSWER(String cORRECTANSWER) {
        this.cORRECTANSWER = cORRECTANSWER;
    }

    public String getTOTALQUESTION() {
        return tOTALQUESTION;
    }

    public void setTOTALQUESTION(String tOTALQUESTION) {
        this.tOTALQUESTION = tOTALQUESTION;
    }

    public String getATTEMPTEDANSWER() {
        return aTTEMPTEDANSWER;
    }

    public void setATTEMPTEDANSWER(String aTTEMPTEDANSWER) {
        this.aTTEMPTEDANSWER = aTTEMPTEDANSWER;
    }

    public String getWRONGANSWER() {
        return wRONGANSWER;
    }

    public void setWRONGANSWER(String wRONGANSWER) {
        this.wRONGANSWER = wRONGANSWER;
    }

    public String getSTATUS() {
        return sTATUS;
    }

    public void setSTATUS(String sTATUS) {
        this.sTATUS = sTATUS;
    }

    public String getTIMETAKEN() {
        return tIMETAKEN;
    }

    public void setTIMETAKEN(String tIMETAKEN) {
        this.tIMETAKEN = tIMETAKEN;
    }

    public String getPYQPTESTNAME() {
        return pYQPTESTNAME;
    }

    public void setPYQPTESTNAME(String pYQPTESTNAME) {
        this.pYQPTESTNAME = pYQPTESTNAME;
    }

}

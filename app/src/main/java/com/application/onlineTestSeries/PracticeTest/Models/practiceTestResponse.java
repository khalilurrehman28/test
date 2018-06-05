
package com.application.onlineTestSeries.PracticeTest.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class practiceTestResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("Max_id")
    @Expose
    private int maxID;

    @SerializedName("testid")
    @Expose
    private int testID;

    @SerializedName("Data")
    @Expose
    private List<practiceTestData> data = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public practiceTestResponse() {
    }

    /**
     * 
     * @param status
     * @param data
     * @param code
     * @param msg
     */
    public practiceTestResponse(Boolean status, Integer code, String msg, List<practiceTestData> data) {
        super();
        this.status = status;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<practiceTestData> getData() {
        return data;
    }

    public void setData(List<practiceTestData> data) {
        this.data = data;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }
}

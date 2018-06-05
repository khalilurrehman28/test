
package com.application.onlineTestSeries.PracticeTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PracticeTestListResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("Data")
    @Expose
    private List<practiceTestListData> data = null;

    @SerializedName("subscription")
    @Expose
    private Integer subscriptionFlag;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<practiceTestListData> getData() {
        return data;
    }

    public void setData(List<practiceTestListData> data) {
        this.data = data;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Integer getSubscriptionFlag() {
        return subscriptionFlag;
    }

    public void setSubscriptionFlag(Integer subscriptionFlag) {
        this.subscriptionFlag = subscriptionFlag;
    }
}

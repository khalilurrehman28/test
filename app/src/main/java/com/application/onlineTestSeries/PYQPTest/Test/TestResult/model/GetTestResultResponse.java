
package com.application.onlineTestSeries.PYQPTest.Test.TestResult.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTestResultResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("outoff")
    @Expose
    private Integer outoff;
    @SerializedName("Data")
    @Expose
    private List<resultData> data = null;

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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getOutoff() {
        return outoff;
    }

    public void setOutoff(Integer outoff) {
        this.outoff = outoff;
    }

    public List<resultData> getData() {
        return data;
    }

    public void setData(List<resultData> data) {
        this.data = data;
    }

}

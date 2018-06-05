
package com.application.onlineTestSeries.PYQPTest.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PYQPTestResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("Data")
    @Expose
    private List<PYQPTestData> data = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PYQPTestResponse() {
    }

    /**
     * 
     * @param status
     * @param data
     * @param code
     * @param msg
     */
    public PYQPTestResponse(Boolean status, String code, String msg, List<PYQPTestData> data) {
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

    public List<PYQPTestData> getData() {
        return data;
    }

    public void setData(List<PYQPTestData> data) {
        this.data = data;
    }

}

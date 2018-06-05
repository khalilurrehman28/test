
package com.application.onlineTestSeries.Legal_maxim.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeximsResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("Data")
    @Expose
    private List<LeximsData> data = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LeximsResponse() {
    }

    /**
     * 
     * @param status
     * @param data
     * @param code
     * @param msg
     */
    public LeximsResponse(Boolean status, Integer code, String msg, List<LeximsData> data) {
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

    public List<LeximsData> getData() {
        return data;
    }

    public void setData(List<LeximsData> data) {
        this.data = data;
    }

}

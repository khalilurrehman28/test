
package com.application.onlineTestSeries.Home.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class subscriptionResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private subscriptionUserData data;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public subscriptionUserData getData() {
        return data;
    }

    public void setData(subscriptionUserData data) {
        this.data = data;
    }

}

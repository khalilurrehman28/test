package com.application.onlineTestSeries.PracticeTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 16/3/18.
 */


public class QuestionUpdateResponse {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     * @param message
     * @param status
     * @param code
     */
    public QuestionUpdateResponse(String code, Boolean status, String message) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
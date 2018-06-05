
package com.application.onlineTestSeries.login.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userData")
    @Expose
    private UserData userData;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LoginData() {
    }

    /**
     * 
     * @param message
     * @param status
     * @param code
     * @param userData
     */
    public LoginData(String code, Boolean status, String message, UserData userData) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
        this.userData = userData;
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

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

}

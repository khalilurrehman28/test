
package com.application.onlineTestSeries.Course.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseResponse {

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
    private List<CourseData> data = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public CourseResponse() {
    }

    /**
     * 
     * @param status
     * @param data
     * @param code
     * @param msg
     */
    public CourseResponse(Boolean status, Integer code, String msg, List<CourseData> data) {
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

    public List<CourseData> getData() {
        return data;
    }

    public void setData(List<CourseData> data) {
        this.data = data;
    }

}

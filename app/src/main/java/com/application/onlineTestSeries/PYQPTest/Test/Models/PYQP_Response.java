
package com.application.onlineTestSeries.PYQPTest.Test.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PYQP_Response {

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
    private List<PYQP_Test_Data> data = null;

    private boolean test;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PYQP_Response() {
    }

    /**
     * 
     * @param status
     * @param data
     * @param code
     * @param msg
     */
    public PYQP_Response(Boolean status, Integer code, String msg, List<PYQP_Test_Data> data) {
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

    public List<PYQP_Test_Data> getData() {
        return data;
    }

    public void setData(List<PYQP_Test_Data> data) {
        this.data = data;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }
}

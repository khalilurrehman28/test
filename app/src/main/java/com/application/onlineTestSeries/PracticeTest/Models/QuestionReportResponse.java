package com.application.onlineTestSeries.PracticeTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionReportResponse {

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
private Boolean data;

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

public Boolean getData() {
return data;
}

public void setData(Boolean data) {
this.data = data;
}

}
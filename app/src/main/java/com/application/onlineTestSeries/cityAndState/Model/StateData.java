
package com.application.onlineTestSeries.cityAndState.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StateData {

    @SerializedName("STATE_ID")
    @Expose
    private String sTATEID;
    @SerializedName("STATE_NAME")
    @Expose
    private String sTATENAME;

    public String getSTATEID() {
        return sTATEID;
    }

    public void setSTATEID(String sTATEID) {
        this.sTATEID = sTATEID;
    }

    public String getSTATENAME() {
        return sTATENAME;
    }

    public void setSTATENAME(String sTATENAME) {
        this.sTATENAME = sTATENAME;
    }


}

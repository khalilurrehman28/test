
package com.application.onlineTestSeries.PYQPTest.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class State {

    @SerializedName("states_id")
    @Expose
    private String statesId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("country_id_fk")
    @Expose
    private String countryIdFk;
    @SerializedName("PYQP_STATE_ID_FK")
    @Expose
    private String pYQPSTATEIDFK;

    public String getStatesId() {
        return statesId;
    }

    public void setStatesId(String statesId) {
        this.statesId = statesId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryIdFk() {
        return countryIdFk;
    }

    public void setCountryIdFk(String countryIdFk) {
        this.countryIdFk = countryIdFk;
    }

    public String getPYQPSTATEIDFK() {
        return pYQPSTATEIDFK;
    }

    public void setPYQPSTATEIDFK(String pYQPSTATEIDFK) {
        this.pYQPSTATEIDFK = pYQPSTATEIDFK;
    }

}

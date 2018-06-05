package com.application.onlineTestSeries.login.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserData extends RealmObject {

    @SerializedName("validity")
    @Expose
    private Integer validity;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("pnone")
    @Expose
    private String pnone;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("dateofbirth")
    @Expose
    private String dateofbirth;
    @PrimaryKey
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("FirstInstall")
    @Expose
    private String firstInstall;

    /**
     * No args constructor for use in serialization
     */
    public UserData() {
    }

    /**
     * @param startDate
     * @param firstInstall
     * @param email
     * @param validity
     * @param userId
     * @param pnone
     * @param name
     * @param image
     * @param gender
     * @param active
     * @param dateofbirth
     */
    public UserData(Integer validity, String startDate, String active, String name, String email, String pnone, String gender, String image, String dateofbirth, String userId, String firstInstall) {
        super();
        this.validity = validity;
        this.startDate = startDate;
        this.active = active;
        this.name = name;
        this.email = email;
        this.pnone = pnone;
        this.gender = gender;
        this.image = image;
        this.dateofbirth = dateofbirth;
        this.userId = userId;
        this.firstInstall = firstInstall;
    }

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPnone() {
        return pnone;
    }

    public void setPnone(String pnone) {
        this.pnone = pnone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstInstall() {
        return firstInstall;
    }

    public void setFirstInstall(String firstInstall) {
        this.firstInstall = firstInstall;
    }

}

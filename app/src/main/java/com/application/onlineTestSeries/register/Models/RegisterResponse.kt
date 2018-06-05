package com.application.onlineTestSeries.register.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by khalil on 6/3/18.
 */
class RegisterResponse {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    constructor()

}
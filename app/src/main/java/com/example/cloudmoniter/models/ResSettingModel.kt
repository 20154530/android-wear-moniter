package com.example.cloudmoniter.models

import com.example.cloudmoniter.contract.CloudResourceType
import com.google.gson.annotations.SerializedName

data class ResSettingModel (

    @SerializedName("res_name")
    var resName: String,

    @SerializedName("res_type")
    var resType: CloudResourceType,

    @SerializedName("secret_key")
    var secretKey: String,

    @SerializedName("secret_id")
    var secretId: String,

    @SerializedName("service_type")
    var serviceType: String,

    @SerializedName("action")
    var action: String,

    @SerializedName("match_full")
    var matchFull: String,

    @SerializedName("match_used")
    var matchUsed: String,

)
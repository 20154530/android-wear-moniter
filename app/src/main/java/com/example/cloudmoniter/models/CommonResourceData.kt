package com.example.cloudmoniter.models

import com.google.gson.annotations.SerializedName

data class CommonResourceData(

    @SerializedName("res_name")
    var resName: String,

    @SerializedName("sub_info")
    var subInfo: String,

    @SerializedName("percentage")
    var percentage: Float,

    @SerializedName("full")
    var full: Long,
)
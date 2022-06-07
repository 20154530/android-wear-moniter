package com.example.cloudmoniter.models.Tencent

import com.google.gson.annotations.SerializedName

data class Response (
    @SerializedName("RequestId") val requestId : String,
    @SerializedName("TrafficPackages") val trafficPackages : List<TrafficPackages>,
    @SerializedName("TotalCount") val totalCount : Long,
    @SerializedName("ExpiringCount") val expiringCount : Long,
    @SerializedName("EnabledCount") val enabledCount : Long,
    @SerializedName("PaidCount") val paidCount : Long
)
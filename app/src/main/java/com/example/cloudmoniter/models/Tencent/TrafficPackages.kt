package com.example.cloudmoniter.models.Tencent

import com.google.gson.annotations.SerializedName

data class TrafficPackages (
    @SerializedName("Id") val id : Long,
    @SerializedName("Type") val type : String,
    @SerializedName("ConfigId") val configId : Long,
    @SerializedName("Bytes") val bytes : Long,
    @SerializedName("BytesUsed") val bytesUsed : Long,
    @SerializedName("Status") val status : String,
    @SerializedName("CreateTime") val createTime : String,
    @SerializedName("EnableTime") val enableTime : String,
    @SerializedName("ExpireTime") val expireTime : String,
    @SerializedName("ContractExtension") val contractExtension : Boolean,
    @SerializedName("AutoExtension") val autoExtension : Boolean,
    @SerializedName("ExtensionMode") val extensionMode : Long,
    @SerializedName("Area") val area : String,
    @SerializedName("LifeTimeMonth") val lifeTimeMonth : String,
    @SerializedName("RefundAvailable") val refundAvailable : Boolean,
    @SerializedName("Channel") val channel : String,
    @SerializedName("ExtensionAvailable") val extensionAvailable : Boolean,
    @SerializedName("Region") val region : String
)
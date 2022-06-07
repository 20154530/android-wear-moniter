package com.example.cloudmoniter.models.kiwivm

import com.google.gson.annotations.SerializedName

data class CloudInfoModel (

    @SerializedName("vm_type") var vmType : String,
    @SerializedName("hostname") var hostname : String,
    @SerializedName("node_ip") var nodeIp : String,
    @SerializedName("node_alias") var nodeAlias : String,
    @SerializedName("node_location") var nodeLocation : String,
    @SerializedName("node_location_id") var nodeLocationId : String,
    @SerializedName("node_datacenter") var nodeDatacenter : String,
    @SerializedName("location_ipv6_ready") var locationIpv6Ready : Boolean,
    @SerializedName("plan") var plan : String,
    @SerializedName("plan_monthly_data") var planMonthlyData : Long,
    @SerializedName("monthly_data_multiplier") var monthlyDataMultiplier : Long,
    @SerializedName("plan_disk") var planDisk : Long,
    @SerializedName("plan_ram") var planRam : Long,
    @SerializedName("plan_swap") var planSwap : Long,
    @SerializedName("plan_max_ipv6s") var planMaxIpv6s : Long,
    @SerializedName("os") var os : String,
    @SerializedName("email") var email : String,
    @SerializedName("data_counter") var dataCounter : Long,
    @SerializedName("data_next_reset") var dataNextReset : Long,
    @SerializedName("ip_addresses") var ipAddresses : List<String>,
    @SerializedName("private_ip_addresses") var privateIpAddresses : List<String>,
    @SerializedName("ip_nullroutes") var ipNullroutes : List<String>,
    @SerializedName("iso1") var iso1 : String,
    @SerializedName("iso2") var iso2 : String,
    @SerializedName("available_isos") var availableIsos : List<String>,
    @SerializedName("plan_private_network_available") var planPrivateNetworkAvailable : Boolean,
    @SerializedName("location_private_network_available") var locationPrivateNetworkAvailable : Boolean,
    @SerializedName("rdns_api_available") var rdnsApiAvailable : Boolean,
    @SerializedName("ptr") var ptr : Any,
    @SerializedName("suspended") var suspended : Boolean,
    @SerializedName("policy_violation") var policyViolation : Boolean,
    @SerializedName("suspension_count") var suspensionCount : String,
    @SerializedName("total_abuse_points") var totalAbusePoints : Int,
    @SerializedName("max_abuse_points") var maxAbusePoints : Int,
    @SerializedName("error") var error : Int

)


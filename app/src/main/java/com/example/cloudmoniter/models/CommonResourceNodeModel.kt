package com.example.cloudmoniter.models

import java.util.Date

class CommonResourceNodeModel {

    /**
     * 资源名称
     */
    var name: String? = null

    /**
     * 资源总量
     */
    var resQuantity: Long = 0

    /**
     * 资源用量
     */
    var dataCounter: Long = 0

    /**
     * 资源使用百分比
     */
    val percentage: Float
        get() = if (resQuantity == 0.toLong()) 0f else dataCounter / resQuantity.toFloat();

    /**
     * 单位
     */
    var unit: String? = null

    /**
     * 到期时间
     */
    var timeLimit: Date? = null
}
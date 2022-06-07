package com.example.cloudmoniter.contract

import com.example.cloudmoniter.models.CommonResourceNodeModel
import com.example.cloudmoniter.models.ResSettingModel

interface IDataUpdater {

    fun DoUpdate(config: ResSettingModel): CommonResourceNodeModel?

    fun formatInfo(node: CommonResourceNodeModel?): String

    fun percentage(node: CommonResourceNodeModel?): Float;
}
package com.example.cloudmoniter.adapters

import com.example.cloudmoniter.contract.IDataUpdater
import com.example.cloudmoniter.models.CommonResourceNodeModel
import com.example.cloudmoniter.models.ResSettingModel

class LocalTimerAdapter : IDataUpdater {

    private lateinit var _config: ResSettingModel;

    override fun DoUpdate(config: ResSettingModel): CommonResourceNodeModel? {
        _config = config;

        val model = CommonResourceNodeModel();
        model.name = config.resName;

        return model;
    }

    override fun formatInfo(node: CommonResourceNodeModel?): String {

        return "";
    }

    override fun percentage(node: CommonResourceNodeModel?): Float {

        return 0f;
    }
}
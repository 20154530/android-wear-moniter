package com.example.cloudmoniter.models

import android.graphics.Color
import com.example.cloudmoniter.adapters.KiwivmAdapter
import com.example.cloudmoniter.adapters.TencentCDNAdapter
import com.example.cloudmoniter.contract.CloudResourceType
import com.example.cloudmoniter.contract.IDataUpdater
import java.util.*

class CloudResNodeViewModel {

    companion object {
        private var _typedic = mapOf(
            CloudResourceType.KiwivmVps to KiwivmAdapter::class.java.name,
            CloudResourceType.TencentCloudCDN to TencentCDNAdapter::class.java.name
        )
    }

    var Id: Int? = 0;

    private var _type = CloudResourceType.Unknown;
    var restype: CloudResourceType
        get() = _type
        set(value) {
            _type = value;
        }

    private var _name = "None";
    var name: String
        get() = _name
        set(value) {
            _name = value;
        }

    private var _subInfo = ""
    var subInfo: String
        get() =  _subInfo
        set(value) {
            _subInfo = value
        }

    private var _percentage = 0f;
    var percentage: Float
        get() = _percentage
        set(value) {
            _percentage = value;
        }

    private var _full: Long = 0;
    var full: Long
        get() = _full;
        set(value) {
            _full = value;
        }

    private var _detail: CommonResourceNodeModel? = null;
    var detail: CommonResourceNodeModel?
        get() = _detail;
        set(value) {
            _detail = value;
        }

    var config: ResSettingModel? = null;

    var updaterType: String? = null;

    private var _updater: IDataUpdater? = null;
    val updater: IDataUpdater?
        get() {
            if (_updater == null) {
                if (updaterType == null) return null;
                _updater = updaterType?.let { Class.forName(it).newInstance() } as IDataUpdater;
                return _updater;
            } else
                return _updater;
        }

    constructor() {}
    constructor(config: ResSettingModel) {
        _name = config.resName
        _type = config.resType
        this.config = config
        if (_typedic.containsKey(config.resType))
            this.updaterType = _typedic[_type]
    }
}
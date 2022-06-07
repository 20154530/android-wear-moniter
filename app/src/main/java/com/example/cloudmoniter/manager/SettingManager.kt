package com.example.cloudmoniter.manager

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.telephony.mbms.MbmsErrors.InitializationErrors
import android.view.View
import android.widget.ArrayAdapter
import com.example.cloudmoniter.contract.CloudResourceType
import com.example.cloudmoniter.contract.IUpdater
import com.example.cloudmoniter.databinding.CloudResNodeBinding
import com.example.cloudmoniter.models.CloudResNodeViewModel
import com.example.cloudmoniter.models.CommonResourceData
import com.example.cloudmoniter.models.ResSettingModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SettingManager private constructor() {

    companion object {
        private val CONFIG_KEY = "cloudmonitor_config";
        private val CONFIG_SETTING_KEY = "resource_node_setting";
        private val CONFIG_DEFAULT_DATA_KEY = "resource_node_default_data";

        private var _instance: SettingManager? = null;
        public val Singleton: SettingManager
            get() {
                if (_instance == null)
                    _instance = SettingManager();
                return _instance!!;
            }
    }

    var ResourceConfigs: ArrayList<CloudResNodeViewModel> = ArrayList()
    var ResourceDefaultData: ArrayList<CommonResourceData> = ArrayList()
    lateinit var ResourceDefaultDataMap: HashMap<String, CommonResourceData>;
    private lateinit var _settings: SharedPreferences;
    private lateinit var _lockStatus: MutableMap<String, Boolean>;
    private lateinit var _adapter: ArrayAdapter<CloudResNodeViewModel>

    private fun appendSettingNode() {
        val node = CloudResNodeViewModel();
        node.name = "None";
        node.restype = CloudResourceType.Unknown;
        this.ResourceConfigs.add(node)
    }

    fun Init(context: Context) {
        ResourceDefaultDataMap = HashMap();
        _settings = context.getSharedPreferences(CONFIG_KEY, Context.MODE_PRIVATE);
    }

    fun SetAdapter(adapter: ArrayAdapter<CloudResNodeViewModel>) {
        _adapter = adapter;
    }

    fun LoadConfig() {
        val configjson = _settings.getString(CONFIG_SETTING_KEY, null);
        val defaultdata = _settings.getString(CONFIG_DEFAULT_DATA_KEY, null);
        if (configjson != null) {
            ResourceConfigs.clear();
            val typeOfObjectsListNew = object : TypeToken<ArrayList<ResSettingModel>?>() {}.type
            val configs = Gson().fromJson<ArrayList<ResSettingModel>>(configjson, typeOfObjectsListNew);
            if (configs != null && configs.count() > 0) {
                ResourceConfigs.addAll(configs.map { c -> CloudResNodeViewModel(c) })
            }

            ResourceDefaultDataMap = HashMap();
            ResourceDefaultData = ArrayList();
            ResourceConfigs.forEach {
                val mod = CommonResourceData(it.name, "", 0f, 0)
                ResourceDefaultData.add(mod)
                ResourceDefaultDataMap[it.name] = mod;
            }

            if (defaultdata != null) {
                val typedefaultdata = object : TypeToken<ArrayList<CommonResourceData>?>() {}.type
                val default:ArrayList<CommonResourceData> = Gson().fromJson(defaultdata, typedefaultdata);
                if(!default.isEmpty()){
                    ResourceDefaultData = default;
                    ResourceDefaultData.forEach {
                        ResourceDefaultDataMap[it.resName] = it
                    }
                    ResourceConfigs.forEach {
                        if (ResourceDefaultDataMap.containsKey(it.name)) {
                            it.full = ResourceDefaultDataMap[it.name]!!.full;
                            it.percentage = ResourceDefaultDataMap[it.name]!!.percentage;
                            it.subInfo = ResourceDefaultDataMap[it.name]!!.subInfo;
                        }
                    }
                }
            }

            appendSettingNode();
        }
        if (ResourceConfigs.isEmpty())
            appendSettingNode();
    }

    fun UseConfig(url: String, context: Context) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val getTask = Callable {
            return@Callable CommonManager().GetInfo(url);
        }
        val future = executor.submit(getTask)
        val configs = future.get(5, TimeUnit.SECONDS)
        if (configs != null) {
            val editor = _settings.edit();
            editor.putString(CONFIG_SETTING_KEY, configs)
            editor.putString(CONFIG_DEFAULT_DATA_KEY, null)
            editor.apply();
        }
        LoadConfig();
        _adapter.notifyDataSetChanged();
    }

    @Synchronized
    private fun queryStatus(name: String, updater: IUpdater?) {
        this._lockStatus[name] = true;
        if (_lockStatus.values.all { s -> s }) {
            updater?.isUpdating = false;
            val editor = _settings.edit();
            editor.putString(CONFIG_DEFAULT_DATA_KEY, Gson().toJson(ResourceDefaultData))
            editor.apply();
        }
    }

    fun UpdateAll(updater: IUpdater?) {
        _lockStatus = HashMap()
        val updatetask = ArrayList<Thread>();
        for (node: CloudResNodeViewModel in ResourceConfigs) {
            _lockStatus[node.name] = false;
            updatetask.add(Thread {
                val nodeadapter = node.updater;
                if (nodeadapter != null) {
                    val info = nodeadapter.DoUpdate(node.config!!);
                    if (info != null) {
                        node.full = info.resQuantity;
                        node.detail = info;
                        node.subInfo = nodeadapter.formatInfo(info)
                        node.percentage = nodeadapter.percentage(info)
                        ResourceDefaultDataMap[node.name]!!.full = node.full;
                        ResourceDefaultDataMap[node.name]!!.subInfo = node.subInfo;
                        ResourceDefaultDataMap[node.name]!!.percentage = node.percentage;

                    }
                    queryStatus(node.name, updater);
                } else {
                    queryStatus(node.name, updater);
                }
            });
        }
        updatetask.forEach { it.start() }
    }

    fun UpdateOne(node: CloudResNodeViewModel, binding: CloudResNodeBinding) {
        val handle = Handler(binding.infoName.context.mainLooper);
        binding.infoProcess.isEnabled = false;
        binding.infoProcess.visibility = View.INVISIBLE;
        binding.infoLoading.visibility = View.VISIBLE;
        Thread {
            var updater = node.updater;
            if (updater != null) {
                val info = updater.DoUpdate(node.config!!);
                if (info != null) {
                    node.full = info.resQuantity;
                    node.detail = info;
                    node.subInfo = updater.formatInfo(info)
                    node.percentage = updater.percentage(info)
                    ResourceDefaultDataMap[node.name]!!.full = node.full;
                    ResourceDefaultDataMap[node.name]!!.subInfo = node.subInfo;
                    ResourceDefaultDataMap[node.name]!!.percentage = node.percentage;

                    val editor = _settings.edit();
                    editor.putString(CONFIG_DEFAULT_DATA_KEY, Gson().toJson(ResourceDefaultData))
                    editor.apply();

                    handle.post {
                        binding.infoSub.text = node.subInfo;
                        binding.infoName.text = node.name;
                        binding.infoProcess.percentage = node.percentage
                    }
                }
            }
            handle.post {
                binding.infoProcess.visibility = View.VISIBLE;
                binding.infoProcess.isEnabled = true;
                binding.infoLoading.visibility = View.INVISIBLE;
            }
        }.start();
    }
}
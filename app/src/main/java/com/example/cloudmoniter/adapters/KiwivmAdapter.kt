package com.example.cloudmoniter.adapters

import com.example.cloudmoniter.Utils
import com.example.cloudmoniter.contract.IDataUpdater
import com.example.cloudmoniter.manager.CommonManager
import com.example.cloudmoniter.models.kiwivm.CloudInfoModel
import com.example.cloudmoniter.models.CommonResourceNodeModel
import com.example.cloudmoniter.models.ResSettingModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.*
import javax.net.ssl.HttpsURLConnection

class KiwivmAdapter : IDataUpdater {

    override fun DoUpdate(config: ResSettingModel): CommonResourceNodeModel? {
        var model = CommonResourceNodeModel();

        var executor: ExecutorService = Executors.newSingleThreadExecutor()
        val updateTask = CloudInfoQueryTask();
        updateTask.config = config;

        var future: Future<CloudInfoModel?> = executor.submit(updateTask)
        var info: CloudInfoModel? = null;
        val name = (CloudInfoModel::class.java).name;
        var modelinfo = Class.forName(name);
        try {
            info = future.get(5, TimeUnit.SECONDS)
            if (info != null) {
                var firstinfo = modelinfo.getDeclaredField(config.matchFull);
                firstinfo.isAccessible = true;
                var secondinfo = modelinfo.getDeclaredField(config.matchUsed);
                secondinfo.isAccessible = true;
                model.resQuantity = firstinfo.getLong(info);
                model.dataCounter = secondinfo.getLong(info);
            }
        } catch (ex: Exception) {
            future.cancel(true)
            return null;
        }

        return model;
    }

    override fun formatInfo(node: CommonResourceNodeModel?): String {
        if (node == null) {
            return "";
        }
        val full = Utils.GetFriendlyByte(node.resQuantity, 1024);
        val last = Utils.GetFriendlyByte(node.resQuantity - node.dataCounter, 1024);
        var str = "$last / $full"
        return str;
    }

    override fun percentage(node: CommonResourceNodeModel?): Float {
        if (node == null) return 0f;
        return 1 - node.percentage;
    }

    internal class CloudInfoQueryTask : Callable<CloudInfoModel?> {
        lateinit var config: ResSettingModel;
        @Throws(Exception::class)
        override fun call(): CloudInfoModel? {
            val conn =
                "https://api.64clouds.com/v1/${config.serviceType}?veid=${config.secretId}&api_key=${config.secretKey}";
            val infomodel = CommonManager().GetInfo(CloudInfoModel::class.java, conn);
            return infomodel;
        }
    }

}
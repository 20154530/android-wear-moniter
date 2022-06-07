package com.example.cloudmoniter.adapters

import android.annotation.SuppressLint
import com.example.cloudmoniter.Utils
import com.example.cloudmoniter.Utils.Companion.bin2hex
import com.example.cloudmoniter.Utils.Companion.hmac256
import com.example.cloudmoniter.Utils.Companion.sha256Hex
import com.example.cloudmoniter.contract.IDataUpdater
import com.example.cloudmoniter.manager.tencent.TencentRequestManager
import com.example.cloudmoniter.models.CommonResourceNodeModel
import com.example.cloudmoniter.models.ResSettingModel
import com.example.cloudmoniter.models.Tencent.TencentCDNPackageQueryModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.net.ssl.HttpsURLConnection


class TencentCDNAdapter : IDataUpdater {

    override fun DoUpdate(config: ResSettingModel): CommonResourceNodeModel? {
        var model = CommonResourceNodeModel();

        var executor: ExecutorService = Executors.newSingleThreadExecutor()
        val task = CloudInfoQueryTask();
        task.config = config;
        var future: Future<TencentCDNPackageQueryModel?> = executor.submit(task)
        var info: TencentCDNPackageQueryModel? = null;

        try {
            info = future.get(5, TimeUnit.SECONDS)
            if (info != null) {
                var package1 = info.response.trafficPackages?.first();
                if (package1 != null) {
                    model.resQuantity = package1.bytes
                    model.dataCounter = package1.bytesUsed
                }
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
        val full = Utils.GetFriendlyByte(node.resQuantity, 1000);
        val last = Utils.GetFriendlyByte(node.resQuantity - node.dataCounter, 1000);
        return "$last / $full"
    }

    override fun percentage(node: CommonResourceNodeModel?): Float {
        if (node == null) return 0f;
        return 1 - node.percentage;
    }

    internal class CloudInfoQueryTask : Callable<TencentCDNPackageQueryModel?> {
        lateinit var config: ResSettingModel;

        @SuppressLint("SimpleDateFormat")
        @Throws(Exception::class)
        override fun call(): TencentCDNPackageQueryModel? {
            val version = "2018-06-06"
            val algorithm = "TC3-HMAC-SHA256"

            val info = TencentRequestManager(version, algorithm)
            val data = info.GetInfo(
                TencentCDNPackageQueryModel::class.java,
                config.secretKey,
                config.secretId,
                config.action,
                config.serviceType
            )

            return data;
        }

    }


}
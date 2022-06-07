package com.example.cloudmoniter.manager.tencent

import android.annotation.SuppressLint
import com.example.cloudmoniter.Utils
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

class TencentRequestManager(apiver: String, algorithm: String) {

    private val _apiversion: String = apiver
    private val _algorithm: String = algorithm;
    private val _hostbase: String = "tencentcloudapi.com";
    private val _contentType: String = "application/json"

    @SuppressLint("SimpleDateFormat")
    public fun <T> GetInfo(
        clazz:Class<T>,
        secretKey: String,
        secretId: String,
        action: String,
        service: String,
        payload: String? = null
    ): T? {
        val SECRET_KEY = secretKey;
        val SECRET_ID = secretId;
        val host = "$service.$_hostbase"
        val version = _apiversion
        val algorithm = _algorithm

        val timestamp = System.currentTimeMillis() / 1000
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.format(Date(("" + timestamp + "000").toLong()))

        val httpRequestMethod = "POST"
        val canonicalUri = "/"
        val canonicalQueryString = ""
        val canonicalHeaders = "content-type:$_contentType\nhost:$host\n"
        val signedHeaders = "content-type;host"

        var actpayload = payload;
        if (actpayload == null) {
            actpayload = "{}";
        }
        val hashedRequestPayload = Utils.sha256Hex(actpayload)
        val canonicalRequest =
            "$httpRequestMethod\n$canonicalUri\n$canonicalQueryString\n$canonicalHeaders\n$signedHeaders\n$hashedRequestPayload"

        val credentialScope = "$date/$service/tc3_request"
        val hashedCanonicalRequest = Utils.sha256Hex(canonicalRequest)
        val stringToSign = "$algorithm\n$timestamp\n$credentialScope\n$hashedCanonicalRequest"

        val secretDate = Utils.hmac256(("TC3" + SECRET_KEY).toByteArray(Charsets.UTF_8), date)
        val secretService = Utils.hmac256(secretDate, service)
        val secretSigning = Utils.hmac256(secretService, "tc3_request")
        val signature: String = Utils.bin2hex(Utils.hmac256(secretSigning, stringToSign)!!)!!

        val authorization =
            ("$algorithm Credential=$SECRET_ID/$credentialScope,SignedHeaders=$signedHeaders,Signature=$signature")

        var conn: HttpsURLConnection =
            URL("https://$host/").openConnection() as HttpsURLConnection;
        conn.requestMethod = "POST"
        conn.setRequestProperty("Authorization", authorization)
        conn.setRequestProperty("Content-Type", _contentType)
        conn.setRequestProperty("Host", host)
        conn.setRequestProperty("X-TC-Action", action)
        conn.setRequestProperty("X-TC-Timestamp", timestamp.toString())
        conn.setRequestProperty("X-TC-Version", version)
        conn.setRequestProperty("X-TC-Language", "zh-CN")
        conn.setDoOutput(true);

        val jsonInputString = "{}";
        conn.getOutputStream().use { os ->
            val input: ByteArray = jsonInputString.toByteArray(Charsets.UTF_8)
            os.write(input, 0, input.size)
        }

        try {
            BufferedReader(
                InputStreamReader(conn.getInputStream(), "utf-8")
            ).use { br ->
                val response = StringBuilder()
                var responseLine: String? = null
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim { it <= ' ' })
                }
                val msg = response.toString();
                return Gson().fromJson(msg, clazz);
            }
        } catch (ex: java.lang.Exception) {
            return null;
        }
    }

}
package com.example.cloudmoniter.manager

import com.example.cloudmoniter.models.kiwivm.CloudInfoModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CommonManager {

    fun <T> GetInfo(
        clazz: Class<T>,
        url: String,
        method: String? = "GET",
        content: String? = null
    ): T? {
        try {
            val infojson = GetInfo(url,method,content);
            val infomodel = Gson().fromJson(infojson, clazz)
            return infomodel;
        } catch (ex: java.lang.Exception) {
            return null;
        }
    }

    fun GetInfo(
        url: String,
        method: String? = "GET",
        content: String? = null
    ): String? {

        try {
            val conn: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection;
            conn.requestMethod = method;
            if (method == "POST") {
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setDoOutput(true)
            }
            if (content != null) {
                val jsonInputString = content;
                conn.getOutputStream().use { os ->
                    val input: ByteArray = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) { // success
                val buffer = BufferedReader(
                    InputStreamReader(conn.inputStream)
                )
                var inputLine: String?
                val response = StringBuffer()
                while (buffer.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                buffer.close()
                val info = response.toString();
                return info;
            }
            return null;
        } catch (ex: java.lang.Exception) {
            return null;
        }
    }

}
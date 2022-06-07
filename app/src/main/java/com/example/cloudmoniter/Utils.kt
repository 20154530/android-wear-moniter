package com.example.cloudmoniter

import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Utils {
    companion object {

        val UnitDic = mapOf(
            1 to "B",
            1.shl(1) to "KB",
            1.shl(2) to "MB",
            1.shl(3) to "GB",
        )

        fun GetFriendlyByte(byte: Long, digit: Int?): String {
            var unit = 1;
            val adigit = if (digit == null) 1024f else digit.toFloat();
            var num = byte.toFloat();
            while (num / adigit > 1) {
                num /= adigit;
                unit = unit.shl(1);
            }
            var unitstr = "B";
            if (UnitDic.containsKey(unit))
                unitstr = UnitDic[unit]!!;
            return "${Math.round(num * 100) / 100f}$unitstr";
        }

        fun hmac256(key: ByteArray?, msg: String): ByteArray? {
            try {
                val hashingAlgorithm = "HmacSHA256" //or "HmacSHA1", "HmacSHA512"
                val bytes = hmac(hashingAlgorithm, key, msg.toByteArray())
                return bytes;
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null;
        }

        @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
        fun hmac(algorithm: String?, key: ByteArray?, message: ByteArray?): ByteArray {
            val mac = Mac.getInstance(algorithm)
            mac.init(SecretKeySpec(key, algorithm))
            return mac.doFinal(message)
        }

        fun sha256Hex(password: String): String? {
            return try {
                var digest: MessageDigest? = null
                try {
                    digest = MessageDigest.getInstance("SHA-256")
                } catch (e1: NoSuchAlgorithmException) {
                    e1.printStackTrace()
                }
                digest!!.reset()
                bin2hex(digest.digest(password.toByteArray()))
            } catch (ignored: java.lang.Exception) {
                null
            }
        }

        fun bin2hex(data: ByteArray): String? {
            val hex = java.lang.StringBuilder(data.size * 2)
            for (b in data) hex.append(String.format("%02x", b.toInt() and 0xFF))
            return hex.toString()
        }

    }

    constructor() {

    }
}
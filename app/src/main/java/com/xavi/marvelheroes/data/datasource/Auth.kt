package com.xavi.marvelheroes.data.datasource

import com.xavi.marvelheroes.BuildConfig
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Calendar
import java.util.Locale

internal class Auth {
    companion object {
        private const val SIG_NUM = 1
        private const val RADIX = 16
        private const val LENGTH = 32
    }

    val ts = "${Calendar.getInstance().timeInMillis}"
    val hash: String
        get() = "$ts${BuildConfig.KEY_PRIVATE}$apikey".md5()
    val apikey: String
        get() = BuildConfig.KEY_PUBLIC

    private fun String.md5(): String {
        val byteData = this.trim()
            .lowercase(Locale.ROOT)
            .toByteArray()
        val digest = MessageDigest.getInstance("MD5").digest(byteData)
        var md5 = BigInteger(SIG_NUM, digest).toString(RADIX)
        while (md5.length < LENGTH) md5 = "0$md5"
        return md5
    }
}

package com.mihaiapps.securevault.bl

import android.util.Base64
import java.nio.charset.Charset

val utf8Charset: Charset = Charset.forName("UTF8")
fun ByteArray.encodeToBase64(): String {
    return Base64.encodeToString(this, Base64.DEFAULT)
}

fun String.toByteArrayUtf8(): ByteArray {
    return this.toByteArray(utf8Charset)
}
fun ByteArray.toStringUtf8(): String {
    return this.toString(utf8Charset)
}

fun String.decodeBase64(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}
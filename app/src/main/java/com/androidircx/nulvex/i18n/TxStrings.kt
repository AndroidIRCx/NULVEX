package com.androidircx.nulvex.i18n

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.security.MessageDigest

private fun txKey(text: String): String {
    val digest = MessageDigest.getInstance("SHA-1")
        .digest(text.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
        .take(10)
    return "tx_auto_$digest"
}

fun Context.tx(text: String): String {
    val key = txKey(text)
    val resId = resources.getIdentifier(key, "string", packageName)
    return if (resId != 0) resources.getString(resId) else text
}

@Composable
fun tx(text: String): String {
    val context = LocalContext.current
    val resId = remember(text, context) {
        val key = txKey(text)
        context.resources.getIdentifier(key, "string", context.packageName)
    }
    return if (resId != 0) stringResource(id = resId) else text
}


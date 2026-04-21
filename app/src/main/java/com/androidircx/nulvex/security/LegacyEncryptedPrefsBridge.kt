package com.androidircx.nulvex.security

import android.content.Context
import android.content.SharedPreferences

internal object LegacyEncryptedPrefsBridge {
    fun open(context: Context, fileName: String): SharedPreferences? {
        return runCatching {
            val appContext = context.applicationContext
            val masterKeyClass = Class.forName("androidx.security.crypto.MasterKey")
            val masterKeyBuilderClass = Class.forName("androidx.security.crypto.MasterKey\$Builder")
            val keySchemeClass = Class.forName("androidx.security.crypto.MasterKey\$KeyScheme")
            val encryptedPrefsClass = Class.forName("androidx.security.crypto.EncryptedSharedPreferences")
            val prefKeySchemeClass = Class.forName(
                "androidx.security.crypto.EncryptedSharedPreferences\$PrefKeyEncryptionScheme"
            )
            val prefValueSchemeClass = Class.forName(
                "androidx.security.crypto.EncryptedSharedPreferences\$PrefValueEncryptionScheme"
            )

            val builder = masterKeyBuilderClass
                .getConstructor(Context::class.java)
                .newInstance(appContext)
            val keyScheme = enumConstant(keySchemeClass, "AES256_GCM")
            masterKeyBuilderClass
                .getMethod("setKeyScheme", keySchemeClass)
                .invoke(builder, keyScheme)
            val masterKey = masterKeyBuilderClass.getMethod("build").invoke(builder)

            val prefKeyScheme = enumConstant(prefKeySchemeClass, "AES256_SIV")
            val prefValueScheme = enumConstant(prefValueSchemeClass, "AES256_GCM")
            val createMethod = encryptedPrefsClass.getMethod(
                "create",
                Context::class.java,
                String::class.java,
                masterKeyClass,
                prefKeySchemeClass,
                prefValueSchemeClass
            )
            createMethod.invoke(
                null,
                appContext,
                fileName,
                masterKey,
                prefKeyScheme,
                prefValueScheme
            ) as SharedPreferences
        }.getOrNull()
    }

    private fun enumConstant(enumClass: Class<*>, name: String): Any {
        val constants = requireNotNull(enumClass.enumConstants) {
            "${enumClass.name} is not an enum"
        }
        return constants.first { constant ->
            (constant as Enum<*>).name == name
        }
    }
}

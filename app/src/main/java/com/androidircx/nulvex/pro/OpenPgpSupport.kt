package com.androidircx.nulvex.pro

import org.bouncycastle.bcpg.ArmoredInputStream
import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.openpgp.PGPSecretKeyRing
import org.bouncycastle.openpgp.PGPObjectFactory
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import java.io.ByteArrayInputStream

data class ParsedPgpKey(
    val format: String,
    val fingerprint: String,
    val keyMaterial: ByteArray
)

object OpenPgpSupport {
    fun parseArmoredKey(armored: String): ParsedPgpKey {
        val bytes = armored.toByteArray(Charsets.UTF_8)
        val input = ArmoredInputStream(ByteArrayInputStream(bytes))
        val factory = PGPObjectFactory(input, JcaKeyFingerprintCalculator())

        var obj: Any? = factory.nextObject()
        while (obj != null) {
            when (obj) {
                is PGPPublicKeyRing -> {
                    val pub = obj.publicKey
                    val fp = pub.fingerprint.toHex()
                    return ParsedPgpKey(
                        format = "pgp_public",
                        fingerprint = fp,
                        keyMaterial = obj.encoded
                    )
                }

                is PGPSecretKeyRing -> {
                    val pub = obj.secretKey.publicKey
                    val fp = pub.fingerprint.toHex()
                    return ParsedPgpKey(
                        format = "pgp_secret",
                        fingerprint = fp,
                        keyMaterial = obj.encoded
                    )
                }
            }
            obj = factory.nextObject()
        }
        throw IllegalArgumentException("Invalid OpenPGP armored key")
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}

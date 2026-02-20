package com.androidircx.nulvex.pro

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.HashAlgorithmTags
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPKeyRingGenerator
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPSignature
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.security.KeyPairGenerator
import java.security.Security
import java.util.Date

class OpenPgpSupportTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupProvider() {
            if (Security.getProvider("BC") == null) {
                Security.addProvider(BouncyCastleProvider())
            }
        }
    }

    @Test
    fun parseArmoredKey_parsesSecretKeyRing() {
        val armoredSecret = generateArmoredSecretKey()

        val parsed = OpenPgpSupport.parseArmoredKey(armoredSecret)

        assertEquals("pgp_secret", parsed.format)
        assertTrue(parsed.fingerprint.isNotBlank())
        assertTrue(parsed.keyMaterial.isNotEmpty())
    }

    @Test
    fun parseArmoredKey_rejectsInvalidInput() {
        assertThrows(IllegalArgumentException::class.java) {
            OpenPgpSupport.parseArmoredKey("not a pgp key")
        }
    }

    private fun generateArmoredSecretKey(): String {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(1024)
        val keyPair = generator.generateKeyPair()
        val nowDate = Date()
        val calc = JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1)
        val pgpKeyPair = JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, keyPair, nowDate)
        val signer = JcaPGPContentSignerBuilder(pgpKeyPair.publicKey.algorithm, HashAlgorithmTags.SHA256)
        val ringGen = PGPKeyRingGenerator(
            PGPSignature.POSITIVE_CERTIFICATION,
            pgpKeyPair,
            "test <test@nulvex>",
            calc,
            null,
            null,
            signer,
            null
        )
        val secretRing = ringGen.generateSecretKeyRing()

        val out = ByteArrayOutputStream()
        ArmoredOutputStream(out).use { armored ->
            armored.write(secretRing.encoded)
        }
        return out.toString(Charsets.UTF_8.name())
    }
}

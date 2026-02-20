package com.androidircx.nulvex.security

import org.bouncycastle.crypto.SecretWithEncapsulation
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Security
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement

data class HybridKemKeyPair(
    val x25519PublicKey: ByteArray,
    val x25519PrivateKey: ByteArray,
    val mlKem768PublicKey: ByteArray,
    val mlKem768PrivateKey: ByteArray
)

data class HybridKemEncapsulation(
    val senderX25519EphemeralPublicKey: ByteArray,
    val mlKem768Ciphertext: ByteArray,
    val sharedSecret: ByteArray
)

object HybridKemService {
    private const val PROVIDER_BC = "BC"

    init {
        if (Security.getProvider(PROVIDER_BC) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    fun generateKeyPair(random: SecureRandom = SecureRandom()): HybridKemKeyPair {
        val x25519Kpg = KeyPairGenerator.getInstance("X25519", PROVIDER_BC)
        val x25519Pair = x25519Kpg.generateKeyPair()

        val mlkemKpg = MLKEMKeyPairGenerator()
        mlkemKpg.init(MLKEMKeyGenerationParameters(random, MLKEMParameters.ml_kem_768))
        val mlkemPair = mlkemKpg.generateKeyPair()
        val mlkemPub = mlkemPair.public as MLKEMPublicKeyParameters
        val mlkemPriv = mlkemPair.private as MLKEMPrivateKeyParameters

        return HybridKemKeyPair(
            x25519PublicKey = x25519Pair.public.encoded,
            x25519PrivateKey = x25519Pair.private.encoded,
            mlKem768PublicKey = mlkemPub.encoded,
            mlKem768PrivateKey = mlkemPriv.encoded
        )
    }

    fun encapsulate(
        recipientX25519PublicKey: ByteArray,
        recipientMlKem768PublicKey: ByteArray,
        random: SecureRandom = SecureRandom()
    ): HybridKemEncapsulation {
        val x25519Kpg = KeyPairGenerator.getInstance("X25519", PROVIDER_BC)
        val ephemeral = x25519Kpg.generateKeyPair()

        val recipientPublic = decodeX25519Public(recipientX25519PublicKey)
        val classicalSecret = deriveX25519Secret(ephemeral.private.encoded, recipientPublic.encoded)

        val pqPublic = MLKEMPublicKeyParameters(MLKEMParameters.ml_kem_768, recipientMlKem768PublicKey)
        val kemGen = MLKEMGenerator(random)
        val wrapped: SecretWithEncapsulation = kemGen.generateEncapsulated(pqPublic)
        val pqSecret = wrapped.secret
        val pqCiphertext = wrapped.encapsulation
        wrapped.destroy()

        val hybrid = combineSecrets(classicalSecret, pqSecret)
        classicalSecret.fill(0)
        pqSecret.fill(0)

        return HybridKemEncapsulation(
            senderX25519EphemeralPublicKey = ephemeral.public.encoded,
            mlKem768Ciphertext = pqCiphertext,
            sharedSecret = hybrid
        )
    }

    fun decapsulate(
        recipientX25519PrivateKey: ByteArray,
        recipientMlKem768PrivateKey: ByteArray,
        senderX25519EphemeralPublicKey: ByteArray,
        mlKem768Ciphertext: ByteArray
    ): ByteArray {
        val senderPublic = decodeX25519Public(senderX25519EphemeralPublicKey)
        val classicalSecret = deriveX25519Secret(recipientX25519PrivateKey, senderPublic.encoded)

        val pqPrivate = MLKEMPrivateKeyParameters(MLKEMParameters.ml_kem_768, recipientMlKem768PrivateKey)
        val extractor = MLKEMExtractor(pqPrivate)
        val pqSecret = extractor.extractSecret(mlKem768Ciphertext)

        val hybrid = combineSecrets(classicalSecret, pqSecret)
        classicalSecret.fill(0)
        pqSecret.fill(0)
        return hybrid
    }

    private fun deriveX25519Secret(privateKey: ByteArray, peerPublicKey: ByteArray): ByteArray {
        val factory = KeyFactory.getInstance("X25519", PROVIDER_BC)
        val priv = factory.generatePrivate(PKCS8EncodedKeySpec(privateKey))
        val pub = factory.generatePublic(X509EncodedKeySpec(peerPublicKey))
        val ka = KeyAgreement.getInstance("X25519", PROVIDER_BC)
        ka.init(priv)
        ka.doPhase(pub, true)
        return ka.generateSecret()
    }

    private fun decodeX25519Public(encoded: ByteArray) =
        KeyFactory.getInstance("X25519", PROVIDER_BC).generatePublic(X509EncodedKeySpec(encoded))

    private fun combineSecrets(classical: ByteArray, pq: ByteArray): ByteArray {
        val ikm = classical + pq
        val out = Hkdf.deriveKey(
            ikm = ikm,
            salt = "nulvex-hybrid-kem-salt-v1".toByteArray(Charsets.UTF_8),
            info = "nulvex-hybrid-kem-master-v1".toByteArray(Charsets.UTF_8),
            length = 32
        )
        ikm.fill(0)
        return out
    }
}

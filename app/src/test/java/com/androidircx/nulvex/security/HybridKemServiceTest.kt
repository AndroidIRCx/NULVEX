package com.androidircx.nulvex.security

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class HybridKemServiceTest {

    @Test
    fun encapsulateAndDecapsulate_produceSameSharedSecret() {
        val receiver = HybridKemService.generateKeyPair()

        val encapsulated = HybridKemService.encapsulate(
            recipientX25519PublicKey = receiver.x25519PublicKey,
            recipientMlKem768PublicKey = receiver.mlKem768PublicKey
        )

        val decapsulated = HybridKemService.decapsulate(
            recipientX25519PrivateKey = receiver.x25519PrivateKey,
            recipientMlKem768PrivateKey = receiver.mlKem768PrivateKey,
            senderX25519EphemeralPublicKey = encapsulated.senderX25519EphemeralPublicKey,
            mlKem768Ciphertext = encapsulated.mlKem768Ciphertext
        )

        assertArrayEquals(encapsulated.sharedSecret, decapsulated)
        assertEquals(32, decapsulated.size)
    }

    @Test
    fun separateEncapsulations_produceDifferentSecrets() {
        val receiver = HybridKemService.generateKeyPair()

        val first = HybridKemService.encapsulate(receiver.x25519PublicKey, receiver.mlKem768PublicKey)
        val second = HybridKemService.encapsulate(receiver.x25519PublicKey, receiver.mlKem768PublicKey)

        assertFalse(first.sharedSecret.contentEquals(second.sharedSecret))
    }
}

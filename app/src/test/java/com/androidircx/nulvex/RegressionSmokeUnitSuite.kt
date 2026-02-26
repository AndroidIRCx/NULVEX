package com.androidircx.nulvex

import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCryptoTest
import com.androidircx.nulvex.data.NoteRepositoryTest
import com.androidircx.nulvex.data.SelfDestructServiceTest
import com.androidircx.nulvex.data.VaultSessionManagerTest
import com.androidircx.nulvex.reminder.NoteReminderSchedulerContractTest
import com.androidircx.nulvex.security.HkdfTest
import com.androidircx.nulvex.security.VaultKeyManagerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Fast JVM suite for critical regression checks.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    XChaCha20Poly1305NoteCryptoTest::class,
    HkdfTest::class,
    VaultKeyManagerTest::class,
    NoteRepositoryTest::class,
    SelfDestructServiceTest::class,
    VaultSessionManagerTest::class,
    NoteReminderSchedulerContractTest::class
)
class RegressionSmokeUnitSuite

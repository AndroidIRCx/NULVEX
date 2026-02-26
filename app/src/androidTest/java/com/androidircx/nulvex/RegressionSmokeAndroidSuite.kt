package com.androidircx.nulvex

import com.androidircx.nulvex.data.DatabaseSchemaCompatibilityTest
import com.androidircx.nulvex.data.NoteDaoTest
import com.androidircx.nulvex.security.PanicWipeServiceTest
import com.androidircx.nulvex.security.VaultAuthServiceTest
import com.androidircx.nulvex.ui.MainViewModelLabelsAndExpiryEditTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Instrumented suite focused on critical app behavior.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    DatabaseSchemaCompatibilityTest::class,
    NoteDaoTest::class,
    VaultAuthServiceTest::class,
    PanicWipeServiceTest::class,
    MainViewModelLabelsAndExpiryEditTest::class
)
class RegressionSmokeAndroidSuite

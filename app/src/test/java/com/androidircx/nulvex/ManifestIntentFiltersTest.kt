package com.androidircx.nulvex

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ManifestIntentFiltersTest {

    @Test
    fun manifest_containsEncryptedFileTypeIntentFiltersAndDeepLink() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        require(manifestFile.exists()) { "AndroidManifest.xml not found at ${manifestFile.path}" }
        val manifest = manifestFile.readText()

        assertTrue(manifest.contains("application/x-nulvex-note"))
        assertTrue(manifest.contains("application/x-nulvex-backup"))
        assertTrue(manifest.contains("application/x-nulvex-keys"))
        assertTrue(manifest.contains("android:pathPattern=\".*\\\\.nulvex\""))
        assertTrue(manifest.contains("android:pathPattern=\".*\\\\.nulvxbk\""))
        assertTrue(manifest.contains("android:pathPattern=\".*\\\\.nulvxkeys\""))
        assertTrue(manifest.contains("android:host=\"androidircx.com\""))
        assertTrue(manifest.contains("android:pathPrefix=\"/api/media/\""))
    }
}

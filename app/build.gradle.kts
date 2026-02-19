import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    jacoco
}

val versionPropsFile = rootProject.file("version.properties")
val versionProps = Properties()
if (versionPropsFile.exists()) {
    versionProps.load(FileInputStream(versionPropsFile))
} else {
    versionProps.setProperty("versionCode", "1")
    versionProps.setProperty("versionName", "1.0")
    versionProps.store(FileOutputStream(versionPropsFile), null)
}
val versionCodeValue = versionProps.getProperty("versionCode")?.toIntOrNull() ?: 1
val versionNameValue = versionProps.getProperty("versionName") ?: "1.0"

val keystorePropsFile = rootProject.file("secrets/keystore.properties")
val keystoreProps = Properties()
if (keystorePropsFile.exists()) {
    keystoreProps.load(FileInputStream(keystorePropsFile))
}

android {
    namespace = "com.androidircx.nulvex"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.androidircx.nulvex"
        minSdk = 26
        targetSdk = 36
        versionCode = versionCodeValue
        versionName = versionNameValue

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (keystoreProps.isNotEmpty()) {
            create("release") {
                storeFile = rootProject.file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            ndk {
                // Upload native debug symbols to Play Console for readable crash reports.
                // SYMBOL_TABLE = function names only (smaller); FULL = + line numbers.
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            if (keystoreProps.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/NOTICE.md"
        }
    }
}

tasks.register("incrementVersionCode") {
    doLast {
        // Bump versionCode (always increments: 7 → 8 → 9 …)
        val nextCode = (versionProps.getProperty("versionCode")?.toIntOrNull() ?: 1) + 1
        versionProps.setProperty("versionCode", nextCode.toString())

        // Bump versionName patch segment (1.02 → 1.03, 1.09 → 1.10, 1.99 → 2.00)
        val currentName = versionProps.getProperty("versionName") ?: "1.00"
        val parts = currentName.split(".")
        val major = parts.getOrNull(0)?.toIntOrNull() ?: 1
        val patch = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val nextPatch = patch + 1
        val nextName = if (nextPatch > 99) "${major + 1}.00" else "$major.${nextPatch.toString().padStart(2, '0')}"
        versionProps.setProperty("versionName", nextName)

        versionProps.store(FileOutputStream(versionPropsFile), null)
    }
}

tasks.matching { it.name == "bundleRelease" }.configureEach {
    dependsOn("incrementVersionCode")
}

jacoco {
    toolVersion = "0.8.12"
}

val jacocoDebugExec = layout.buildDirectory.file("jacoco/testDebugUnitTest.exec")
val jacocoDebugAltExec = layout.buildDirectory.file(
    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
)
val debugKotlinClassesDir = layout.buildDirectory.dir(
    "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"
)
val debugJavaClassesDir = layout.buildDirectory.dir(
    "intermediates/javac/debug/compileDebugJavaWithJavac/classes"
)

tasks.register<JacocoReport>("jacocoDebugUnitTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val excludes = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*\$Lambda$*.*",
        "**/*Companion*.*"
    )

    classDirectories.setFrom(
        files(
            fileTree(debugKotlinClassesDir) { exclude(excludes) },
            fileTree(debugJavaClassesDir) { exclude(excludes) }
        )
    )
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(files(jacocoDebugExec, jacocoDebugAltExec))
}

tasks.register<JacocoCoverageVerification>("jacocoDebugUnitTestCoverageVerification") {
    dependsOn("testDebugUnitTest")
    dependsOn("jacocoDebugUnitTestReport")

    val excludes = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*\$Lambda$*.*",
        "**/*Companion*.*"
    )

    classDirectories.setFrom(
        files(
            fileTree(debugKotlinClassesDir) { exclude(excludes) },
            fileTree(debugJavaClassesDir) { exclude(excludes) }
        )
    )
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(files(jacocoDebugExec, jacocoDebugAltExec))

    val minimumLineCoverage = (findProperty("coverage.minimum.line") as String?) ?: "0.05"

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = BigDecimal(minimumLineCoverage)
            }
        }
    }
}

tasks.named("check") {
    dependsOn("jacocoDebugUnitTestCoverageVerification")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.sqlite)
    implementation(libs.sqlcipher)
    implementation(libs.argon2kt)
    implementation(libs.tink.android)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.biometric)
    implementation(libs.play.services.ads)
    implementation(libs.billing.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.core)
    testImplementation("org.json:json:20240303")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.work.testing)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

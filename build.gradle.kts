// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val androidGradlePluginVersion = "9.2.1"
    val patchedBouncyCastleVersion = "1.84"
    val bouncyCastleBuildscriptModules = setOf(
        "bcpkix-jdk18on",
        "bcprov-jdk18on",
        "bcutil-jdk18on"
    )

    dependencies {
        classpath("com.android.tools.build:gradle:$androidGradlePluginVersion") {
            exclude(group = "org.bouncycastle", module = "bcpkix-jdk18on")
            exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
            exclude(group = "org.bouncycastle", module = "bcutil-jdk18on")
        }
        classpath("org.bouncycastle:bcpkix-jdk18on:$patchedBouncyCastleVersion")
        classpath("org.bouncycastle:bcprov-jdk18on:$patchedBouncyCastleVersion")
        classpath("org.bouncycastle:bcutil-jdk18on:$patchedBouncyCastleVersion")

        components {
            all {
                if (id.group != "org.bouncycastle") {
                    allVariants {
                        withDependencies {
                            if (any { it.group == "org.bouncycastle" && it.name in bouncyCastleBuildscriptModules }) {
                                removeAll { it.group == "org.bouncycastle" && it.name in bouncyCastleBuildscriptModules }
                                add("org.bouncycastle:bcpkix-jdk18on:$patchedBouncyCastleVersion")
                                add("org.bouncycastle:bcprov-jdk18on:$patchedBouncyCastleVersion")
                                add("org.bouncycastle:bcutil-jdk18on:$patchedBouncyCastleVersion")
                            }
                        }
                    }
                }
            }
        }
    }
    configurations.classpath {
        resolutionStrategy {
            force("org.bouncycastle:bcpkix-jdk18on:$patchedBouncyCastleVersion")
            force("org.bouncycastle:bcprov-jdk18on:$patchedBouncyCastleVersion")
            force("org.bouncycastle:bcutil-jdk18on:$patchedBouncyCastleVersion")
            force("io.netty:netty-codec-http:4.2.13.Final")
            force("io.netty:netty-codec-http2:4.2.13.Final")
        }
    }
}

plugins {
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.7" apply false
}

subprojects {
    plugins.withId("com.android.application") {
        dependencies {
            constraints {
                add("implementation", "com.google.protobuf:protobuf-java:4.34.1")
                add("implementation", "com.google.protobuf:protobuf-javalite:4.34.1")
                add("implementation", "com.google.protobuf:protobuf-kotlin:4.34.1")
                add("implementation", "com.google.protobuf:protobuf-kotlin-lite:4.34.1")
                add("implementation", "io.netty:netty-handler:4.2.13.Final")
                add("implementation", "io.netty:netty-codec-http2:4.2.13.Final")
                add("implementation", "io.netty:netty-codec-http:4.2.13.Final")
                add("implementation", "io.netty:netty-codec:4.2.13.Final")
                add("implementation", "io.netty:netty-common:4.2.13.Final")
                add("implementation", "org.jdom:jdom2:2.0.6.1")
                add("implementation", "org.bitbucket.b_c:jose4j:0.9.6")
                add("implementation", "org.apache.commons:commons-lang3:3.20.0")
                add("implementation", "org.apache.httpcomponents:httpclient:4.5.14")
                add("implementation", "com.google.guava:guava:33.6.0-android")
            }
        }
    }
}

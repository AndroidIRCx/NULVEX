pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
buildscript {
    val patchedBouncyCastleVersion = "1.84"
    val bouncyCastleBuildscriptModules = setOf(
        "bcpkix-jdk18on",
        "bcprov-jdk18on",
        "bcutil-jdk18on"
    )

    dependencies {
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
            force("io.netty:netty-codec-http:4.2.12.Final")
            force("io.netty:netty-codec-http2:4.2.12.Final")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NULVEX"
include(":app")

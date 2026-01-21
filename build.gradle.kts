// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

subprojects {
    configurations.configureEach {
        resolutionStrategy {
            force(
                "com.google.protobuf:protobuf-java:4.28.3",
                "com.google.protobuf:protobuf-javalite:4.28.3",
                "com.google.protobuf:protobuf-kotlin:4.28.3",
                "com.google.protobuf:protobuf-kotlin-lite:4.28.3",
                "io.netty:netty-codec-http2:4.1.112.Final",
                "io.netty:netty-handler:4.1.112.Final",
                "org.jdom:jdom2:2.0.6.1"
            )
        }
    }
}

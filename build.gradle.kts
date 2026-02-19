// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

subprojects {
    plugins.withId("com.android.application") {
        dependencies {
            constraints {
                add("implementation", "com.google.protobuf:protobuf-java:4.33.4")
                add("implementation", "com.google.protobuf:protobuf-javalite:4.33.4")
                add("implementation", "com.google.protobuf:protobuf-kotlin:4.33.4")
                add("implementation", "com.google.protobuf:protobuf-kotlin-lite:4.33.4")
                add("implementation", "io.netty:netty-handler:4.1.129.Final")
                add("implementation", "io.netty:netty-codec-http2:4.1.129.Final")
                add("implementation", "io.netty:netty-codec-http:4.1.129.Final")
                add("implementation", "io.netty:netty-codec:4.1.129.Final")
                add("implementation", "io.netty:netty-common:4.1.129.Final")
                add("implementation", "org.jdom:jdom2:2.0.6.1")
                add("implementation", "org.bitbucket.b_c:jose4j:0.9.6")
                add("implementation", "org.apache.commons:commons-lang3:3.18.0")
                add("implementation", "org.apache.httpcomponents:httpclient:4.5.14")
                add("implementation", "com.google.guava:guava:33.5.0-android")
            }
        }
    }
}

# ─── Nulvex ProGuard / R8 Rules ─────────────────────────────────────────────

# ─── Room (entities, DAOs, database) ────────────────────────────────────────
# Room maps column names via reflection at runtime; obfuscating field names
# on @Entity classes will break the schema mapping.
-keep @androidx.room.Entity class ** { *; }
-keep @androidx.room.Dao interface ** { *; }
-keep class * extends androidx.room.RoomDatabase {
    public static ** INSTANCE;
    public static ** Companion;
}

# ─── WorkManager workers ────────────────────────────────────────────────────
# WorkManager instantiates workers via reflection using the 2-arg constructor.
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ─── SQLCipher (JNI native bridge) ──────────────────────────────────────────
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# ─── Argon2Kt (JNI native bridge) ───────────────────────────────────────────
-keep class com.lambdapioneer.argon2kt.** { *; }
-keepclassmembers class com.lambdapioneer.argon2kt.** {
    native <methods>;
}

# ─── Google Tink (crypto primitives) ────────────────────────────────────────
# Tink registers key managers via reflection; strip nothing from tink packages.
-keep class com.google.crypto.tink.** { *; }
-keep interface com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# ─── Kotlin coroutines ───────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ─── Kotlin metadata / reflection attributes ─────────────────────────────────
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions
-keepattributes EnclosingMethod

# ─── Stack traces: keep line numbers (helps crash reports) ──────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

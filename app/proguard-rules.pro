# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Strip Debug and Info logs in Release (AGP 9.1+ named level syntax)
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# --- Gson ---
# Keep fields annotated with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Shizuku & AIDL IPC ---
# Keep all generated classes from AIDL files
-keep interface com.hypercapsule.app.** { *; }
-keep class com.hypercapsule.app.**$Stub { *; }
-keep class com.hypercapsule.app.**$Proxy { *; }

# Ensure our privileged service implementation is not stripped or renamed
-keep class com.hypercapsule.app.integration.shizuku.PrivilegedServiceImpl { *; }

# Kotlin Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Data and Theme Models from being stripped
-keep class com.hypercapsule.app.models.** { *; }
-keep class com.hypercapsule.app.data.model.** { *; }
-keep class com.hypercapsule.app.ui.model.** { *; }


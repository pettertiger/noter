# Proguard configuration for Noter app

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Dropbox SDK
-keep class com.dropbox.** { *; }
-dontwarn com.dropbox.**

# Google APIs
-keep class com.google.api.** { *; }
-keep class com.google.auth.** { *; }
-dontwarn com.google.**

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Hilt
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# GSON
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

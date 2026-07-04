# ─────────────────────────────────────────────────────────────
# Filmora ProGuard Rules
# ─────────────────────────────────────────────────────────────

# Preserve line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ─── Retrofit ────────────────────────────────────────────────
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ─── OkHttp ──────────────────────────────────────────────────
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ─── Gson ────────────────────────────────────────────────────
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ─── Data Models (Keep all model classes for Gson/Retrofit) ──
-keep class azari.amirhossein.filmora.models.** { *; }
-keepclassmembers class azari.amirhossein.filmora.models.** { *; }

# ─── Room ────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.**

# ─── Hilt / Dagger ───────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint *;
}
-dontwarn dagger.hilt.**

# ─── Coroutines ──────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ─── DataStore ───────────────────────────────────────────────
-keep class androidx.datastore.** { *; }

# ─── Navigation (SafeArgs) ───────────────────────────────────
-keep class * extends androidx.navigation.NavArgs { *; }
-keepnames class * extends androidx.navigation.NavDirections { *; }

# ─── Coil ────────────────────────────────────────────────────
-dontwarn coil.**

# ─── Lottie ──────────────────────────────────────────────────
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ─── Shimmer ─────────────────────────────────────────────────
-keep class com.facebook.shimmer.** { *; }

# ─── Flexbox ─────────────────────────────────────────────────
-dontwarn com.google.android.flexbox.**

# ─── SimpleRatingBar ─────────────────────────────────────────
-dontwarn com.ome450901.simplratingbar.**

# ─── Paging ──────────────────────────────────────────────────
-keep class androidx.paging.** { *; }

# ─── Parcelize ───────────────────────────────────────────────
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ─── ViewBinding / DataBinding ───────────────────────────────
-keep class * extends androidx.viewbinding.ViewBinding { *; }
-dontwarn androidx.databinding.**
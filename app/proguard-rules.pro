# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Retrofit & Gson
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.crossbow.translator.data.api.models.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

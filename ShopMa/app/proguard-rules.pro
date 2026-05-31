# Add project specific ProGuard rules here.
# Keep Retrofit models
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.shopma.app.models.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.google.gson.** { *; }

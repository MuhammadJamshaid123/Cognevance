# Add project specific ProGuard rules here.
-keep class com.cognevance.ecommerce.domain.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

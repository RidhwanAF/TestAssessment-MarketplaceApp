# Dagger/Hilt
-keep class * implements dagger.hilt.internal.GeneratedEntryPoint { <init>(); }
-keep class * implements dagger.hilt.internal.GeneratedComponent { <init>(); }
-keep class * implements dagger.hilt.internal.GeneratedComponentManager { <init>(); }
-keep class * implements dagger.hilt.internal.GeneratedComponentManagerHolder { <init>(); }
-keep class dagger.hilt.internal.processedrootsentinel.codegen.* { *; }
-keep class dagger.hilt.android.HiltAndroidApp
-keep @dagger.hilt.android.WithFragmentBindings class *
-keep @dagger.hilt.android.HiltAndroidApp class *

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coroutine support
-keep class kotlinx.coroutines.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Room
-keepclassmembers class * extends androidx.room.RoomDatabase {
  public static *** CONVERTERS;
}

# DataStore
-keepclassmembers class androidx.datastore.preferences.protobuf.*GeneratedMessageLite {
    <fields>;
}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
-keep,allowoptimization,allowshrinking,allowobfuscation class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowoptimization,allowshrinking,allowobfuscation class retrofit2.Response
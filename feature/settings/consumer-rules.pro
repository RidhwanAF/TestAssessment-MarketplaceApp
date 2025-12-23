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

# DataStore
-keepclassmembers class androidx.datastore.preferences.protobuf.*GeneratedMessageLite {
    <fields>;
}

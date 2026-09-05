# Kotlinx serialization keeps
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.rewindvideo.plex.**$$serializer { *; }
-keepclassmembers class com.rewindvideo.plex.** {
    *** Companion;
}
-keepclasseswithmembers class com.rewindvideo.plex.** {
    kotlinx.serialization.KSerializer serializer(...);
}

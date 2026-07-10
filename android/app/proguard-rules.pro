# Regras do kotlinx.serialization (úteis se você ativar o minify no release)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class com.baralhobiblico.app.game.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.baralhobiblico.app.game.**$$serializer { *; }

# For stack traces
-keepattributes SourceFile, LineNumberTable

#-keep public class io.github.amanshuraikwar.nxtbuz.domain.result.** {
#  public protected *;
#}

-keep public enum io.github.amanshuraikwar.nxtbuz.**{
    *;
}

-keeppackagenames io.github.amanshuraikwar.nxtbuz

-dontwarn kotlin.internal.**
-dontwarn kotlin.reflect.jvm.internal.ReflectionFactoryImpl
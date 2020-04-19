# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# For stack traces
-keepattributes SourceFile, LineNumberTable

# Required for Retrofit/OkHttp
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keepattributes *Annotation*, Signature, Exceptions

# This optimization conflicts with how Retrofit uses proxy objects without concrete implementations
-optimizations !method/removal/parameter

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keep public class io.github.amanshuraikwar.nxtbuz.ui.list.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.busapi.model.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.busarrival.model.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.busstop.model.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.prefs.model.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.user.model.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.room.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.domain.location.model.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.domain.result.** {
  public protected *;
}

-keep public class io.github.amanshuraikwar.nxtbuz.data.SetupState {
  public protected *;
}

-keep public enum io.github.amanshuraikwar.nxtbuz.**{
    *;
}

-keeppackagenames io.github.amanshuraikwar.nxtbuz

-dontwarn kotlin.internal.**
-dontwarn kotlin.reflect.jvm.internal.ReflectionFactoryImpl
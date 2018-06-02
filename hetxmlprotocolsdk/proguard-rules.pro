# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\android-sdk_r24.3.4-windows\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for._bind_webview {
#   public *;
#}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

#Could not instantiate mapper : com.thoughtworks.xstream.mapper.EnumMapper :
# <init> [interface com.thoughtworks.xstream.mapper.Mapper
-dontwarn com.thoughtworks.xstream.**
-dontwarn com.google.gson.stream.**
-dontwarn com.google.gson.**
-keepattributes *Annotation*#使用注解需要添加
-keep class com.thoughtworks.xstream.** { *; }
-keep class android.os.** { *; }
-keep class android.text.** { *; }
-keep class java.lang.** { *; }
-keep class com.google.gson.** {*;}

-keep public class com.het.core.core.protocol.model.** {*;}
-keep public class com.het.core.core.protocol.coder.bean.** {*;}
#-keep public class com.het.core.core.https.HetHttpApi { *; }
## 正式发布要干掉
#-keep public class com.het.core.core.packet.factory.vopen.GenerateOpenPacket {*; }
#-keep public class com.het.core.model.HetCoreModel { *; }
#-keep public class com.het.core.model.PacketBuffer { *; }
#-keep public class com.het.core.utils.Logc { *; }
#
#-keep public class com.het.core.core.https.** { *; }
#-keep public class com.het.core.core.https.HetHttpApi {*;}
#-keep public class com.het.core.core.https.HttpUtils {*;}
#-keep public class com.het.core.core.https.MapKeyComparator {*;}
#去掉日志信息
#-assumenosideeffects class android.util.Log {
#    public static *** v(...);
#    public static *** i(...);
#    public static *** d(...);
#    public static *** w(...);
#    public static *** e(...);
#}


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
-dontwarn com.thoughtworks.xstream.**
-dontwarn com.google.gson.stream.**
-keepattributes *Annotation*#使用注解需要添加
-keep class android.os.** { *; }
-keep public class uuxia.het.com.library.** { *; }
-keep public class com.het.udp.core.Utils.** { *; }
-keep public class com.het.udp.core.observer.** {*;}
-keep public class com.het.udp.core.ServiceManager {*;}
-keep public class com.het.udp.core.smartlink.bind.** {*;}
-keep public class com.het.udp.core.smartlink.callback.** {*;}
-keep public class com.het.udp.core.smartlink.SmartLinkManipualtor {*;}
-keep public class com.het.udp.core.keepalive.OnDeviceOnlineListener { *; }
-keep public class com.het.udp.wifi.callback.OnSendListener { *; }
-keep public class com.het.udp.wifi.protocol.ProtocolManager { *; }
-keep public class com.het.udp.wifi.utils.** { *; }
-keep public class com.het.udp.wifi.packet.factory.vopen.GenerateOpenPacket{*;}
-keep public class com.het.udp.wifi.packet.PacketUtils{*;}
-keep public class com.het.udp.wifi.model.DeviceModel{*;}
-keep public class com.het.udp.wifi.model.PacketModel{*;}
-keep public class com.het.udp.wifi.model.ProtocolDataModel{*;}
-keep public class com.het.udp.wifi.model.ProtocolBean{*;}
-keep class pc.test.reply.** { *; }
#去掉日志信息
#-assumenosideeffects class android.util.Log {
#    public static *** v(...);
#    public static *** i(...);
#    public static *** d(...);
#    public static *** w(...);
#    public static *** e(...);
#}


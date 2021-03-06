# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/bkmsx/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include audioPreview and orderInList by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interfaces
# class:
#-keepclassmembers class fqcn.of.javascript.interfaces.for.webview {
#   public *;
#}
-keep public class com.semantive.waveformandroid.**
-dontwarn java.lang.invoke.**
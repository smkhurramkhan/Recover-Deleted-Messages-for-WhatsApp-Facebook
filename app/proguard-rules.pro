-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keepclasseswithmembers class * extends com.way4net.oner.lifa.plugin.ThemedFragment
-keepclasseswithmembers class * extends com.way4net.oner.lifa.plugin.ThemedActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keepattributes Signature #there were 1 classes trying to access generic signatures using reflection emfehlung von proguard selbst

-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn android.support.v4.**
#-dontwarn javax.annotation.**
#-dontwarn org.xmlpull.v1.**
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-dontnote okhttp3.**
-dontnote org.kobjects.util.**
-dontnote org.xmlpull.v1.**
-keep class okhttp3.** {
      *;
 }

-keep class org.xmlpull.v1.XmlSerializer {
    *;
}
-keep class org.xmlpull.v1.XmlPullParser{
    *;
}
-dontwarn org.xmlpull.v1.XmlPullParser

-keep class org.xmlpull.v1.XmlSerializer {
    *;
}
-dontwarn org.xmlpull.v1.XmlSerializer

-keep class org.kobjects.** { *; }
-keep class org.ksoap2.** { *; }
-keep class okio.** { *; }
-keep class org.kxml2.** { *; }
-keep class org.xmlpull.** { *; }

-keep class android.support.v7.app.AppCompatViewInflater{ <init>(...); }


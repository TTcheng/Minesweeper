# Keep model classes used reflectively by Android framework / libraries.
-keepclassmembers class * extends android.app.Activity { public void *(android.view.View); }
-keep public class com.example.wangchuncheng.minesweeper.** { *; }

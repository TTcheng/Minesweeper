# Minesweeper

A single-Activity Android implementation of Minesweeper. The game is drawn
on a custom `SurfaceView` (`Playground`) that owns the touch handling,
difficulty selection, board generation and win/lose dialogs.

Originally authored in 2017 (`com.example.wangchuncheng.minesweeper`,
`Created by WangChunCheng on 2017/12/16`). The `feat/android-16-migration`
branch is a build-system modernization: the gameplay code is unchanged.

## Gameplay

A normal minesweeper board — tap an unrevealed cell to reveal it. Revealing
a zero-neighbour cell recursively reveals its safe neighbourhood.
Revealing a mine triggers the lose dialog; clearing every safe cell
triggers the win dialog. Both dialogs offer **Continue** (restart) or
**Cancel** (close the app).

Three presets, chosen at start-up:

| Level    | Grid   | Mines |
|----------|--------|-------|
| 简单 (Easy)   | 10 × 10 | 15    |
| 中等 (Medium) | 15 × 15 | 30    |
| 困难 (Hard)   | 15 × 15 | 45    |

The board squares are sized to the screen width; on a phone taller than
it is wide the play area is centred vertically.

## Build

Requirements:

- JDK 17+ (JDK 21 works)
- Android SDK with **platform 36** installed (`compileSdk 36`)
- Gradle 8.10.2 (or the wrapper, generated below)

```bash
# 1. Point Gradle at your Android SDK
cp local.properties.template local.properties
# edit local.properties so that sdk.dir points to your SDK install

# 2. Build the debug APK
gradle wrapper          # generates ./gradlew if you don't have a wrapper yet
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

`compileSdk 36` corresponds to Android 16; `minSdk 21` (Android 5.0) is
the floor. `targetSdk 36` is required to satisfy Android 16's
installation / behaviour policy.

## Install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.wangchuncheng.minesweeper/.MainActivity
```

Or open the project in Android Studio (Hedgehog or newer) and use **Run**.

## Project layout

```
app/
  build.gradle                          AGP module, AndroidX, targetSdk 36
  proguard-rules.pro
  src/main/
    AndroidManifest.xml                 single launcher Activity
    java/com/example/wangchuncheng/minesweeper/
      Box.java                          cell model
      MainActivity.java                 hosts Playground
      Playground.java                   SurfaceView + game loop
    res/
      values/{strings,colors,themes}.xml
      drawable/ic_launcher_foreground.xml
      mipmap-anydpi-v26/ic_launcher{,_round}.xml   adaptive icons (API 26+)
      mipmap-{m,h,xh,xxh,xxxh}dpi/*.png            rasterized fallbacks
build.gradle, settings.gradle, gradle.properties,
gradle/wrapper/gradle-wrapper.properties            Gradle wrapper config
local.properties.template                           SDK path template
.gitignore
```

## Recent changes

`feat/android-16-migration` (commit `668f3b5`) — brought the project from
"bare sources, legacy `android.support.v7`, no Gradle, no resources" to a
buildable Android 16 app. Concretely:

- **Build system**: added `settings.gradle`, root `app/build.gradle`,
  `gradle.properties`, `gradle-wrapper.properties` (Gradle 8.10.2 +
  AGP 8.7.3). One runtime dependency: `androidx.appcompat:appcompat`.
- **Resources**: added `strings.xml`, `colors.xml`, `themes.xml`
  (`Theme.AppCompat.Light.NoActionBar`), and a complete launcher icon
  set (adaptive vector for API 26+, rasterized PNGs for API 21-25).
- **Source ports** (no gameplay change):
  - `MainActivity`: `android.support.v7.app.AppCompatActivity` →
    `androidx.appcompat.app.AppCompatActivity`.
  - `Playground`: replaced `System.exit(0)` in the cancel-button
    callback with `Activity.finish()`; the former is incompatible with
    the modern Android process model and would have killed the SurfaceView
    mid-dismissal.
  - `AndroidManifest.xml`: dropped the legacy `package=` attribute
    (AGP 8+ reads it from `namespace`); added `android:exported="true"`
    on the launcher activity (required since `targetSdk 31`); locked
    `screenOrientation=portrait` and declared `configChanges` so that
    rotation does not destroy the `SurfaceView` and lose game state.

Gameplay, board sizes, mine counts, and difficulty labels are unchanged.

## Notes / known quirks

- The board width is derived solely from `sceenWidth`; on landscape
  devices the play area is clipped. This matches the original 2017
  implementation — fix the math in `Playground.surfaceChanged` if you
  want full landscape support.
- There is no long-press flag (right-click equivalent); only reveal.
- `searchSecuriytyNeighbor` is recursive; very large empty boards can
  stack-overflow. With the current 15×15 max board this is not reached
  in practice.
- No license has been declared in this repository. Treat the source as
  "all rights reserved" by the original author until a `LICENSE` file
  is added.

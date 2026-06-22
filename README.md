# Liquid Glass Music Player (English-only build)

Native Android (Kotlin) music player with a liquid-glass / neon / Gotham-style UI,
that automatically scans the device's local storage for music and plays it.

This build has Persian (Farsi) language, RTL support, and the Vazirmatn font removed —
it is English-only and left-to-right.

## Features

- **Automatic local scan**: Uses `MediaStore` to find every audio file on internal
  storage and SD card, fully offline (`data/MusicScanner.kt`).
- **Local playback**: ExoPlayer (Media3), including background playback.
- **Search**: live filter by title, artist, and album.
- **Liquid Glass UI**: translucent cards with thin neon edges, a glass album-cover
  frame, and a glowing wave-style progress bar.
- **Responsive layout**: built with `ConstraintLayout` and relative width/height
  percentages instead of fixed sizes.

## Project structure

```
app/src/main/java/com/liquidglass/musicplayer/
├── data/
│   ├── Track.kt              Track data model
│   └── MusicScanner.kt       MediaStore-based automatic scanner
├── service/
│   ├── PlaybackService.kt    Background playback service (Media3 Session)
│   └── PlayerHolder.kt       ExoPlayer holder for quick UI access
├── ui/
│   ├── SplashActivity.kt
│   ├── MainActivity.kt       Library + scan + search + mini player
│   ├── NowPlayingActivity.kt Now Playing glass screen
│   └── TrackAdapter.kt
└── util/
    └── PermissionUtil.kt     Handles Android 13+ vs older permissions
```

## Build via GitHub Actions (no local Android Studio needed)

1. Push this folder to a GitHub repository.
2. The included `.github/workflows/build.yml` will automatically build a debug APK.
3. Go to the **Actions** tab → open the latest run → download the
   `app-debug-apk` artifact.
4. Extract it, transfer `app-debug.apk` to your phone, and install
   (allow "install from unknown sources" if prompted).

## Build locally (Android Studio)

1. Open this folder in Android Studio.
2. Wait for Gradle sync.
3. `Build → Build Bundle(s) / APK(s) → Build APK(s)`.
4. APK appears at `app/build/outputs/apk/debug/app-debug.apk`.

## Permissions

- `READ_MEDIA_AUDIO` (Android 13+) or `READ_EXTERNAL_STORAGE` (older)
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` for background playback
- `POST_NOTIFICATIONS` for notification controls

All requested at runtime.

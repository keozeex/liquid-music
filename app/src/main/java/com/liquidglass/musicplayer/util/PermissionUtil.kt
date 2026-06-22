package com.liquidglass.musicplayer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * مدیریت مجوز دسترسی به موزیک‌های دستگاه بسته به نسخه‌ی اندروید.
 * Handles the correct runtime permission depending on Android version
 * (READ_MEDIA_AUDIO for Android 13+, READ_EXTERNAL_STORAGE for older).
 */
object PermissionUtil {

    fun audioPermission(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            audioPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }
}

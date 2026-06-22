package com.liquidglass.musicplayer.data

import android.net.Uri

/**
 * مدل داده‌ی یک آهنگ که از حافظه‌ی دستگاه اسکن شده.
 * Local track model scanned from device storage via MediaStore.
 */
data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUri: Uri,
    val albumArtUri: Uri?,
    val sizeBytes: Long,
    val path: String
) {
    fun durationFormatted(): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}

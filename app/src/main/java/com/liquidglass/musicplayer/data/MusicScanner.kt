package com.liquidglass.musicplayer.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * اسکنر خودکار موزیک‌های لوکال گوشی.
 * با استفاده از MediaStore تمام فایل‌های صوتی روی دستگاه (حافظه‌ی داخلی + کارت حافظه)
 * را بدون نیاز به سرور یا اینترنت پیدا می‌کند.
 *
 * Automatic local music scanner. Uses MediaStore to find every audio file
 * on internal storage + SD card, fully offline.
 */
class MusicScanner(private val context: Context) {

    private val albumArtBaseUri: Uri = Uri.parse("content://media/external/audio/albumart")

    suspend fun scanDevice(): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()

        val collection: Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_MUSIC
        )

        // فقط فایل‌های موزیک واقعی، نه رینگ‌تون یا صدای پیام
        // Only real music files, excluding ringtones / notification sounds / very short clips
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " +
                "${MediaStore.Audio.Media.DURATION} >= 15000"

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol) ?: "بدون عنوان"
                val artist = cursor.getString(artistCol) ?: "هنرمند ناشناس"
                val album = cursor.getString(albumCol) ?: "بدون آلبوم"
                val albumId = cursor.getLong(albumIdCol)
                val duration = cursor.getLong(durationCol)
                val size = cursor.getLong(sizeCol)
                val path = cursor.getString(dataCol) ?: ""

                val contentUri = ContentUris.withAppendedId(collection, id)
                val artUri = ContentUris.withAppendedId(albumArtBaseUri, albumId)

                tracks.add(
                    Track(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        durationMs = duration,
                        contentUri = contentUri,
                        albumArtUri = artUri,
                        sizeBytes = size,
                        path = path
                    )
                )
            }
        }

        tracks
    }

    /** گروه‌بندی آهنگ‌ها بر اساس آلبوم - Group tracks by album */
    suspend fun scanAlbums(): Map<String, List<Track>> = withContext(Dispatchers.IO) {
        scanDevice().groupBy { it.album }
    }

    /** گروه‌بندی آهنگ‌ها بر اساس هنرمند - Group tracks by artist */
    suspend fun scanArtists(): Map<String, List<Track>> = withContext(Dispatchers.IO) {
        scanDevice().groupBy { it.artist }
    }
}

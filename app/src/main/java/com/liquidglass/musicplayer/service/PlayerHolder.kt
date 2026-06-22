package com.liquidglass.musicplayer.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.liquidglass.musicplayer.data.Track

/**
 * نگه‌دارنده‌ی ساده‌ی ExoPlayer برای دسترسی در کل اپ بدون پیچیدگی اضافه.
 * در نسخه‌ی نهایی پیشنهاد می‌شود این بخش به MediaController متصل به
 * PlaybackService ارتقا یابد تا کنترل از نوتیفیکیشن هم کار کند.
 *
 * Lightweight singleton wrapper around ExoPlayer for app-wide access.
 * For production, connect this through a MediaController bound to
 * PlaybackService so notification/lock-screen controls stay in sync.
 */
object PlayerHolder {

    private var player: ExoPlayer? = null
    private var queue: List<Track> = emptyList()
    private var currentIndex: Int = -1

    fun get(context: Context): ExoPlayer {
        return player ?: ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .also { player = it }
    }

    fun playQueue(context: Context, tracks: List<Track>, startIndex: Int) {
        queue = tracks
        currentIndex = startIndex
        val exo = get(context)
        val mediaItems = tracks.map { MediaItem.fromUri(it.contentUri) }
        exo.setMediaItems(mediaItems, startIndex, 0L)
        exo.prepare()
        exo.playWhenReady = true
    }

    fun currentTrack(): Track? = queue.getOrNull(currentIndex)

    fun togglePlayPause() {
        player?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    fun isPlaying(): Boolean = player?.isPlaying == true

    fun next() {
        player?.let {
            if (it.hasNextMediaItem()) {
                it.seekToNextMediaItem()
                currentIndex = (currentIndex + 1).coerceAtMost(queue.lastIndex)
            }
        }
    }

    fun previous() {
        player?.let {
            if (it.hasPreviousMediaItem()) {
                it.seekToPreviousMediaItem()
                currentIndex = (currentIndex - 1).coerceAtLeast(0)
            }
        }
    }

    fun addListener(listener: Player.Listener) {
        player?.addListener(listener)
    }

    fun release() {
        player?.release()
        player = null
    }
}

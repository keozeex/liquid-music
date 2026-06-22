package com.liquidglass.musicplayer.service

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * سرویس پخش موزیک در پس‌زمینه با ExoPlayer + MediaSession.
 * این سرویس اجازه می‌دهد پخش حتی وقتی اپ بسته یا صفحه خاموش است ادامه پیدا کند،
 * و کنترل از نوتیفیکیشن/لاک‌اسکرین هم در دسترس باشد.
 *
 * Background playback service. Keeps audio playing when the app is
 * minimized or the screen is off, and exposes lock-screen / notification controls.
 */
class PlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true) // پاز خودکار وقتی هندزفری قطع می‌شود
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        // اگر چیزی پخش نمی‌شود، با بسته شدن اپ سرویس هم متوقف شود
        val player = mediaSession.player
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }
}

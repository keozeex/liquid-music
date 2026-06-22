package com.liquidglass.musicplayer.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.liquidglass.musicplayer.R
import com.liquidglass.musicplayer.databinding.ActivityNowPlayingBinding
import com.liquidglass.musicplayer.service.PlayerHolder

class NowPlayingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNowPlayingBinding
    private val handler = Handler(Looper.getMainLooper())

    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNowPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindTrackInfo()
        setupControls()

        PlayerHolder.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlayPauseIcon(isPlaying)
            }
        })

        updatePlayPauseIcon(PlayerHolder.isPlaying())
    }

    private fun bindTrackInfo() {
        val track = PlayerHolder.currentTrack()
        if (track != null) {
            binding.trackTitle.text = track.title
            binding.trackArtist.text = "${track.artist} · ${track.album}"
            binding.timeTotal.text = track.durationFormatted()

            Glide.with(this)
                .load(track.albumArtUri)
                .placeholder(R.drawable.ic_album)
                .error(R.drawable.ic_album)
                .centerCrop()
                .into(binding.albumArt)
        }
    }

    private fun setupControls() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnPlayPause.setOnClickListener {
            PlayerHolder.togglePlayPause()
        }

        binding.btnNext.setOnClickListener {
            PlayerHolder.next()
            bindTrackInfo()
        }

        binding.btnPrev.setOnClickListener {
            PlayerHolder.previous()
            bindTrackInfo()
        }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        (binding.btnPlayPause.getChildAt(0) as? android.widget.ImageView)?.setImageResource(icon)
    }

    private fun updateProgress() {
        val player = PlayerHolder.get(this)
        val duration = player.duration.takeIf { it > 0 } ?: return
        val position = player.currentPosition
        val ratio = position.toFloat() / duration.toFloat()
        binding.waveProgress.progress = (ratio * 1000).toInt()
        binding.timeElapsed.text = formatMs(position)
    }

    private fun formatMs(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onResume() {
        super.onResume()
        handler.post(progressRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(progressRunnable)
    }
}

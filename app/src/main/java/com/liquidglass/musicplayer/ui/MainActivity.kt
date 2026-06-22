package com.liquidglass.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.liquidglass.musicplayer.R
import com.liquidglass.musicplayer.data.MusicScanner
import com.liquidglass.musicplayer.data.Track
import com.liquidglass.musicplayer.databinding.ActivityMainBinding
import com.liquidglass.musicplayer.service.PlayerHolder
import com.liquidglass.musicplayer.util.PermissionUtil
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scanner: MusicScanner
    private lateinit var adapter: TrackAdapter

    private var allTracks: List<Track> = emptyList()

    private val permissionLauncher = registerForActivityResultLauncher()

    private fun registerForActivityResultLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                runScan()
            } else {
                binding.scanStatus.visibility = View.VISIBLE
                binding.scanStatus.text = getString(R.string.permission_denied)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scanner = MusicScanner(this)

        adapter = TrackAdapter { track, index ->
            PlayerHolder.playQueue(this, allTracks, index)
            showMiniPlayer(track)
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }
        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = adapter

        binding.scanButton.setOnClickListener { requestScanPermissionAndRun() }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                filterTracks(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.miniPlayer.setOnClickListener {
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }
        binding.miniPlayPause.setOnClickListener {
            PlayerHolder.togglePlayPause()
            updateMiniPlayPauseIcon()
        }

        // اسکن خودکار در اولین اجرا اگر مجوز از قبل موجود است
        if (PermissionUtil.hasAudioPermission(this)) {
            runScan()
        }
    }

    private fun requestScanPermissionAndRun() {
        if (PermissionUtil.hasAudioPermission(this)) {
            runScan()
        } else {
            permissionLauncher.launch(PermissionUtil.audioPermission())
        }
    }

    private fun runScan() {
        binding.scanStatus.visibility = View.VISIBLE
        binding.scanStatus.text = getString(R.string.scan_in_progress)
        binding.emptyState.visibility = View.GONE

        lifecycleScope.launch {
            val tracks = scanner.scanDevice()
            allTracks = tracks
            adapter.submitList(tracks)

            binding.scanStatus.visibility = View.VISIBLE
            binding.scanStatus.text = if (tracks.isEmpty()) {
                getString(R.string.scan_empty)
            } else {
                getString(R.string.scan_done, tracks.size)
            }

            binding.emptyState.visibility = if (tracks.isEmpty()) View.VISIBLE else View.GONE
            binding.trackList.visibility = if (tracks.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun filterTracks(query: String) {
        val filtered = if (query.isBlank()) {
            allTracks
        } else {
            allTracks.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
    }

    private fun showMiniPlayer(track: Track) {
        binding.miniPlayer.visibility = View.VISIBLE
        binding.miniTitle.text = track.title
        binding.miniArtist.text = track.artist
        Glide.with(this)
            .load(track.albumArtUri)
            .placeholder(R.drawable.ic_album)
            .error(R.drawable.ic_album)
            .centerCrop()
            .into(binding.miniArt)
        updateMiniPlayPauseIcon()
    }

    private fun updateMiniPlayPauseIcon() {
        binding.miniPlayPause.setImageResource(
            if (PlayerHolder.isPlaying()) R.drawable.ic_pause else R.drawable.ic_play
        )
    }
}

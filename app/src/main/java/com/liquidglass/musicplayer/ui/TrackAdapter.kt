package com.liquidglass.musicplayer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liquidglass.musicplayer.R
import com.liquidglass.musicplayer.data.Track

class TrackAdapter(
    private val onClick: (Track, Int) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private val items = mutableListOf<Track>()

    fun submitList(tracks: List<Track>) {
        items.clear()
        items.addAll(tracks)
        notifyDataSetChanged()
    }

    fun currentList(): List<Track> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = items[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onClick(track, position) }
    }

    override fun getItemCount(): Int = items.size

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val art: ImageView = itemView.findViewById(R.id.rowArt)
        private val title: TextView = itemView.findViewById(R.id.rowTitle)
        private val artist: TextView = itemView.findViewById(R.id.rowArtist)
        private val duration: TextView = itemView.findViewById(R.id.rowDuration)

        fun bind(track: Track) {
            title.text = track.title
            artist.text = track.artist
            duration.text = track.durationFormatted()

            Glide.with(itemView.context)
                .load(track.albumArtUri)
                .placeholder(R.drawable.ic_album)
                .error(R.drawable.ic_album)
                .centerCrop()
                .into(art)
        }
    }
}

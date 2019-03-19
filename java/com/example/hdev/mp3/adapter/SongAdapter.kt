package com.example.hdev.mp3.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hdev.mp3.R
import com.example.hdev.mp3.models.Song
import kotlinx.android.synthetic.main.item_song.view.text_singer_item
import kotlinx.android.synthetic.main.item_song.view.text_song_item

class SongAdapter(val context: Context, val songs: List<Song>, val onClickItem: (song: Song) -> Unit) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song, parent, false))
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bindView(songs[position])
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(song: Song) {
            itemView.text_song_item.setText(song.getName())
            itemView.text_singer_item.setText(song.getArtist())
            itemView.setOnClickListener { onClickItem(song) }
        }
    }
}

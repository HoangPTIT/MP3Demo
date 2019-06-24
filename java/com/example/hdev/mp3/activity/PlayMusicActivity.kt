package com.example.hdev.mp3.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.hdev.mp3.R
import com.example.hdev.mp3.models.Song
import kotlinx.android.synthetic.main.activity_play_music.text_singer_play
import kotlinx.android.synthetic.main.activity_play_music.text_song_play

class PlayMusicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)
        setUpUi()
    }

    private fun setUpUi(){
        val intent = getIntent()
        val song = intent.getParcelableExtra<Song>(MainActivity.SONG)
        text_singer_play.setText(song.getArtist())
        text_song_play.setText(song.getName())
    }

    //TODO: Action Stop, Start, Next, Back
}

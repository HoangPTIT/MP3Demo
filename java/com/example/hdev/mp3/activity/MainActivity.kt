package com.example.hdev.mp3.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.hdev.mp3.R
import com.example.hdev.mp3.adapter.SongAdapter
import com.example.hdev.mp3.models.Song
import com.example.hdev.mp3.services.PlayMusicService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.recycler_view
import kotlinx.android.synthetic.main.fragment_song_offline.progressBar
import java.io.File
import java.util.StringTokenizer

class MainActivity : AppCompatActivity() {
    private val MP3 = ".mp3"
    private val MP3_ = ".MP3"
    private val PATH = "/Zing MP3/"
    private lateinit var mAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requirePermissions()

        startService(Intent(this, PlayMusicService::class.java))
    }

    private fun requirePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        } else {
            // Granted
            getAllAudioFiles()
        }
    }

    private fun updateUi(songs: List<Song>) {
        mAdapter = SongAdapter(this, songs) { song ->
            onClickSong(song)
            Intent(applicationContext, PlayMusicActivity::class.java).also { intent ->
                intent.putExtra(SONG, song)
                startActivity(intent)
            }
        }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        progressBar.visibility = View.GONE
    }

    // use Broadcast send song
    private fun onClickSong(song: Song) {
        Intent().apply {
            this.setAction(PlayMusicService.RECEIVE_SONG)
            this.putExtra(PlayMusicService.KEY_SONG, song)
            LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(this)
        }
    }

    // Rx
    @SuppressLint("CheckResult")
    private fun getAllAudioFiles() {
        progressBar.visibility = View.VISIBLE
        getAllAudioFilesSingle()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ fileList ->
                val songs = arrayListOf<Song>()
                for (file in fileList) {
                    val stringTokenizer = StringTokenizer(file.name, "_")
                    if (stringTokenizer.countTokens() > 2) {
                        val song =
                            Song(file.absolutePath, stringTokenizer.nextToken(), "", stringTokenizer.nextToken())
                        songs.add(song)
                    }
                }
                updateUi(songs)
            }, { throwable -> Log.e("", throwable.toString()) })
    }

    // Get mp3 from folder Zing mp3
    private fun getAllAudioFilesSingle(): Single<List<File>> {
        return Single.defer {
            val fileList = ArrayList<File>()
            val path = Environment.getExternalStorageDirectory().absolutePath + PATH
            getFile(fileList, File(path))
            Single.just(fileList)
        }
    }

    // Get mp3
    private fun getFile(fileList: ArrayList<File>, dir: File) {
        val listFile = dir.listFiles()
        if (listFile != null && listFile.size > 0) {
            for (i in listFile.indices) {
                if (listFile[i].isDirectory()) {
                    getFile(fileList, listFile[i])
                } else {
                    if (listFile[i].getName().endsWith(MP3) || listFile[i].getName().endsWith(MP3_)) {
                        fileList.add(listFile[i])
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getAllAudioFiles()
                } else {
                    requirePermissions()
                }
                return
            }
        }
    }

    companion object {
        val REQUEST_PERMISSION_CODE = 100
        val SONG = "SONG"
    }
}

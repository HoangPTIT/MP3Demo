package com.example.hdev.mp3.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import com.example.hdev.mp3.R
import com.example.hdev.mp3.activity.MainActivity
import com.example.hdev.mp3.models.Song

class PlayMusicService : Service() {

    private val mMediaPlayer = MediaPlayer()
    private var song: Song? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        configMediaPlayer()

        registerReceivers()
    }

    private fun configMediaPlayer() {
        mMediaPlayer.isLooping = true
    }

    private fun prepareToStart() {
        showNotification()

        stop()

        mMediaPlayer.setDataSource(song?.getaPath())
        mMediaPlayer.prepare()
    }

    private fun start() {
        if (!mMediaPlayer.isPlaying) {
            mMediaPlayer.start()
        }
    }

    private fun pause() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
    }

    private fun stop() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
        }
    }

    private fun close() {
        clearNotification()

        stop()
    }

    private fun registerReceivers() {
        IntentFilter().also { intentFilter ->
            intentFilter.addAction(RECEIVE_SONG)
            intentFilter.addAction(START)
            LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent != null && context != null) {
                        handleEvent(intent, context)
                    }
                }
            }, intentFilter)
        }
    }

    private fun handleEvent(intent: Intent, context: Context) {
        when (intent?.action) {
            RECEIVE_SONG -> {
                song = intent.getParcelableExtra(KEY_SONG)
                prepareToStart()
                start()
            }
            START -> {
                start()
            }
            PAUSE -> {
                pause()
            }
            STOP -> {
                stop()
            }
            CLOSE -> {
                close()
            }
        }
    }

    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.hongnhan)
            .setContentTitle(song?.getName())
            .setContentText(song?.getArtist())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun clearNotification() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
    }

    companion object {
        val RECEIVE_SONG = "RECEIVE_SONG"
        val KEY_SONG = "SONG"
        val START = "START"
        val STOP = "STOP"
        val PAUSE = "PAUSE"
        val CLOSE = "CLOSE"
        val CHANNEL_ID = "CHANNEL_ID"
    }
}

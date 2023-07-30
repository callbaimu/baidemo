package com.luojunkai.app.video

import android.graphics.SurfaceTexture
import android.os.AsyncTask
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.ui.PlayerView
import com.luojunkai.app.R

class VideoPlayerActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    private lateinit var videoPlayer: ExoPlayer
    private lateinit var videoUrl: String
    private lateinit var playerView: PlayerView
    private var isPlayerInitialized = false

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoUrl = intent.getStringExtra("videoUrl") ?: ""

        // 初始化 ExoPlayer
        val trackSelector: TrackSelector = DefaultTrackSelector(this)
        videoPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(buildMediaSourceFactory())
            .build()

        playerView = findViewById(R.id.exoPlayerView)
        playerView.player = videoPlayer

        // 异步加载视频资源
        LoadVideoTask().execute()
    }

    @UnstableApi
    private fun buildMediaSourceFactory(): MediaSourceFactory {
        val defaultDataSourceFactory = DefaultDataSourceFactory(this)
        return DefaultMediaSourceFactory(defaultDataSourceFactory)
    }

    override fun onResume() {
        super.onResume()
        if (isPlayerInitialized) {
            videoPlayer.play()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPlayerInitialized) {
            videoPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (isPlayerInitialized) {
            videoPlayer.release()
            isPlayerInitialized = false
        }
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        // 在此回调方法中初始化 surfaceTexture
        videoPlayer.setVideoSurface(Surface(surfaceTexture))
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        // 不需要处理
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        // 不需要处理
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
        // 不需要处理
    }

    // 静态内部类，避免静态字段泄漏
    private inner class LoadVideoTask : AsyncTask<Void, Void, MediaItem>() {
        override fun doInBackground(vararg params: Void): MediaItem {
            // 加载视频资源，此处为示例，实际中可能需要根据 videoUrl 加载视频资源
            return MediaItem.fromUri(videoUrl)
        }

        override fun onPostExecute(result: MediaItem) {
            // 视频资源加载完成后，准备播放
            videoPlayer.setMediaItem(result)
            videoPlayer.prepare()
            isPlayerInitialized = true
        }
    }
}

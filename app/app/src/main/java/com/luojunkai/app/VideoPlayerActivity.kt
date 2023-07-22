package com.luojunkai.app

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.ui.PlayerView


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

        // 准备视频播放
        val mediaSource = buildMediaSourceFactory().createMediaSource(MediaItem.fromUri(videoUrl))

        videoPlayer.setMediaSource(mediaSource)
        videoPlayer.prepare()
        isPlayerInitialized = true

        // 设置点击事件监听器
        playerView.setOnClickListener {
            if (videoPlayer.isPlaying) {
                videoPlayer.pause()
            } else {
                videoPlayer.play()
            }
        }

        // 添加播放器事件监听器，用于更新播放状态
        videoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && videoPlayer.playWhenReady) {
                    // 播放中
                    // 在此处可以更新播放按钮的状态
                } else if (playbackState == Player.STATE_READY) {
                    // 暂停
                    // 在此处可以更新播放按钮的状态
                } else if (playbackState == Player.STATE_ENDED) {
                    // 播放结束
                    // 在此处可以更新播放按钮的状态
                }
            }

            // 在此处可以添加其他 Player 事件的回调方法
        })
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
        if (isPlayerInitialized) {
            videoPlayer.release()
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
}
package com.andreskaminker.exofireexample

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andreskaminker.exofireexample.databinding.FragmentVideoBinding
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File


class VideoFragment : Fragment() {
    val videoURL = "https://firebasestorage.googleapis.com/v0/b/examplefireba-189ae.appspot.com/o/file.mp4?alt=media&token=ebbbb3c4-abf1-412d-a32b-e987d59cfa6b"
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var player: SimpleExoPlayer? = null
    private  var _binding : FragmentVideoBinding? = null
    val binding: FragmentVideoBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseStorage = Firebase.storage
        val storageReference = firebaseStorage.reference
        val path = storageReference.child("file.mp4")
        val localFile = File.createTempFile("work_video", "mp4")
        player = SimpleExoPlayer.Builder(context!!).build()
        binding.videoView.player = player



    }
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {

            buildMediaSource((Uri.parse(videoURL)))?.let {
                initializePlayer(
                    it
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            buildMediaSource((Uri.parse(videoURL)))?.let {
                initializePlayer(
                    it
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }


    private fun initializePlayer(mediaSource: MediaSource){
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare(mediaSource)
    }

    companion object {
        const val ONETWENTY_MEGA = 120 * 1024 * 1024
        private val TAG = "VideoFragment"
    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(this.requireContext(), "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }
}
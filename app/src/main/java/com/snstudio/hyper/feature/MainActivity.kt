package com.snstudio.hyper.feature

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isInvisible
import androidx.media3.common.util.UnstableApi
import com.snstudio.hyper.R
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.convertToBitmap
import com.snstudio.hyper.core.extension.gone
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.startColorAnimation
import com.snstudio.hyper.core.extension.visible
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.ActivityMainBinding
import com.snstudio.hyper.service.JobCompletedCallback
import com.snstudio.hyper.service.JobService
import com.snstudio.hyper.shared.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), JobCompletedCallback {
    private val viewModel: MainViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mediaDownloadedBefore: Boolean = false
    private var currentDownloadedMediaId: String = ""
    private var currentMedia: Media? = null
    private var animator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vm = mediaViewModel
        binding.lifecycleOwner = this
        viewModel.createMusicFolder()
        observeData()
        initPlayerMenuButtonsListener()
    }

    override fun onJobStart(id: String) {
        currentDownloadedMediaId = id
    }

    override fun onJobProgress(progress: Int) {
        runOnUiThread {
            if (currentDownloadedMediaId == currentMedia?.id) {
                setDownloadButtonStatus(true)
                binding.textProgress.text = getString(R.string.progress, progress)
            } else {
                setDownloadButtonStatus(false)
            }
        }
    }

    override fun onJobCompleted(media: Media) {
        runOnUiThread {
            if (currentDownloadedMediaId == currentMedia?.id) {
                binding.btnDownload.setImageResource(R.drawable.ic_check)
            } else {
                binding.btnDownload.setImageResource(R.drawable.ic_download)
            }
            currentDownloadedMediaId = ""
            setDownloadButtonStatus(false)

            with(media) {
                thumbnail?.convertToBitmap(applicationContext, onSuccess = { bitmap ->
                    val localMedia = media.copy(bitmap = bitmap, type = 0)
                    viewModel.insertMedia(localMedia)
                }, onFailure = {})
            }
        }
    }


    @OptIn(UnstableApi::class)
    private fun observeData() {
        with(mediaViewModel) {
            observe(playerLiveData) { player ->
                binding.playerView.player = player
            }
            observe(playbackStateLiveData) { isLocal ->
                binding.playerMenu.visible()
                binding.btnDownload.isInvisible = isLocal
            }
            observe(playerWhenReadyLiveData) { ready ->
                if (ready) {
                    animator = binding.playerMenu.startColorAnimation()
                }
            }
            observe(currentMediaLiveData) { media ->
                currentMedia = media
                mediaDownloadedBefore = viewModel.mediaIsSaved(media)
                if (mediaDownloadedBefore) {
                    binding.btnDownload.setImageResource(R.drawable.ic_check)
                } else {
                    binding.btnDownload.setImageResource(R.drawable.ic_download)
                }
            }
        }
    }

    private fun initPlayerMenuButtonsListener() {
        with(binding) {
            btnClose.click {
                playerMenu.gone()
                mediaViewModel.stopPlayer()
            }
            btnDownload.click {
                if (mediaDownloadedBefore) {
                    // daha önce indirilmiş
                    return@click
                }
                if (JobService.runningJobCount > 0) {
                    // şu an indirme işlemi yapılıyor
                } else {
                    startDownloadService()
                }
            }
        }
    }

    private fun startDownloadService() {
        currentMedia?.let { media ->
            JobService.download(
                media,
                mediaViewModel.getMediaMetaData(),
                mediaViewModel.getCurrentMediaUrl(),
                this,
                applicationContext,
            )
        }
    }

    private fun setDownloadButtonStatus(status: Boolean) {
        with(binding) {
            btnDownload.isInvisible = status
            textProgress.isInvisible = !status
        }
    }

}
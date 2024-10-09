package com.snstudio.hyper

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.media3.common.util.UnstableApi
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.gone
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.startColorAnimation
import com.snstudio.hyper.databinding.ActivityMainBinding
import com.snstudio.hyper.shared.MediaViewModel
import com.snstudio.hyper.shared.ProgressLiveData
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarLayout
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var animator: ValueAnimator? = null
    private var snackParentView: View? = null

    private val snackProgressBarManager by lazy {
        SnackProgressBarManager(
            binding.root,
            lifecycleOwner = this,
        ).apply {
            setBackgroundColor(R.color.main_color)
            setProgressBarColor(R.color.text_color)
            setProgressTextColor(R.color.text_color)
            setViewToMove(binding.playerView)
            useRoundedCornerBackground(true)
            setMessageMaxLines(1)
        }
    }

    private val progressSnackBar by lazy {
        SnackProgressBar(SnackProgressBar.TYPE_CIRCULAR, getString(R.string.downloading))
            .setIsIndeterminate(false)
            .setProgressMax(100)
            .setAllowUserInput(true)
            .setShowProgressPercentage(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.createMusicFolder()
        observeData()
        initPlayerMenuButtonsListener()
        initSnackDisplayListener()
        with(binding) {
            vm = mediaViewModel
            lifecycleOwner = this@MainActivity
        }
    }

    @OptIn(UnstableApi::class)
    private fun observeData() {
        with(mediaViewModel) {
            observe(playerLiveData) { player ->
                binding.playerView.player = player
            }
            observe(playbackStateLiveData) {
                // binding.playerMenu.visible()
                // moveSnack(380)
            }
            observe(showPlayerMenuLiveData) {
                binding.playerMenu.isVisible = it
                moveSnack(380)
            }
            observe(playerWhenReadyLiveData) { ready ->
                if (ready) {
                    animator = binding.playerMenu.startColorAnimation()
                }
            }
            observe(ProgressLiveData.mediaDownloadStateLiveData) { state ->
                when (state) {
                    is ProgressLiveData.DownloadState.Started -> {
                        snackProgressBarManager.show(
                            progressSnackBar,
                            SnackProgressBarManager.LENGTH_INDEFINITE,
                        )
                        progressSnackBar.setMessage(state.message)
                        snackProgressBarManager.updateTo(progressSnackBar)
                    }

                    is ProgressLiveData.DownloadState.InProgress -> {
                        snackProgressBarManager.setProgress(state.progress)
                    }

                    is ProgressLiveData.DownloadState.Completed -> {
                        snackProgressBarManager.dismiss()
                    }

                    is ProgressLiveData.DownloadState.Failed -> {}
                }
            }
        }
    }

    private fun initSnackDisplayListener() {
        snackProgressBarManager.setOnDisplayListener(
            object :
                SnackProgressBarManager.OnDisplayListener {
                override fun onLayoutInflated(
                    snackProgressBarLayout: SnackProgressBarLayout,
                    overlayLayout: FrameLayout,
                    snackProgressBar: SnackProgressBar,
                    onDisplayId: Int,
                ) {
                    snackParentView = snackProgressBarLayout.parent as View
                    if (binding.playerMenu.isVisible) {
                        moveSnack(380)
                    }
                }
            },
        )
    }

    private fun moveSnack(bottom: Int) {
        val params = snackParentView?.layoutParams as? ViewGroup.MarginLayoutParams
        params?.setMargins(0, 0, 0, bottom)
        snackParentView?.layoutParams = params
    }

    private fun initPlayerMenuButtonsListener() {
        with(binding) {
            btnClose.click {
                mediaViewModel.stopPlayer()
                playerMenu.gone()
                moveSnack(50)
            }
        }
    }
}

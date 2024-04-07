package com.snstudio.hyper.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.UnstableApi
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.snstudio.hyper.R
import com.snstudio.hyper.core.extension.gone
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.startColorAnimation
import com.snstudio.hyper.core.extension.stopColorAnimation
import com.snstudio.hyper.core.extension.visible
import com.snstudio.hyper.databinding.ActivityMainBinding
import com.snstudio.hyper.shared.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var animator: ValueAnimator? = null
    private val mediaViewModel: MediaViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vm = mediaViewModel
        binding.lifecycleOwner = this
        observeData()
        initNavController()
        closePlayerMenu()
    }

    private fun initNavController() {
        val navController = findNavController(R.id.baseNavHost)
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                }

                R.id.searchFragment -> {
                    binding.bottomNavView.visibility = View.GONE
                }
            }
        }
    }


    @OptIn(UnstableApi::class)
    private fun observeData() {
        with(mediaViewModel) {
            observe(playerLiveData) { player ->
                binding.playerView.player = player
            }
            observe(playbackStateLiveData) { isReady ->
                if (isReady) {
                    binding.playerMenu.visible()
                }
            }
            observe(playerWhenReadyLiveData) { ready ->
                if (ready) {
                    animator = binding.playerMenu.startColorAnimation()
                } else {
                    binding.playerMenu.stopColorAnimation(animator)
                }
            }

        }
    }

    private fun closePlayerMenu() {
        with(binding) {
            btnClose.setOnClickListener {
                playerMenu.gone()
                mediaViewModel.stopPlayer()
            }
        }
    }

}
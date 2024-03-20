package com.snstudio.hyper.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.snstudio.hyper.R
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.visibility
import com.snstudio.hyper.databinding.ActivityMainBinding
import com.snstudio.hyper.shared.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mediaViewModel: MediaViewModel
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewModel()
        observeData()
        initNavController()
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

    private fun initViewModel() {
        mediaViewModel = ViewModelProvider(this)[MediaViewModel::class.java]
    }

    @OptIn(UnstableApi::class)
    private fun observeData() {
        with(mediaViewModel) {
            observe(playerLiveData) { player ->
                binding.playerMenu.player = player
            }
            observe(playerVisibleLiveData) { visibility ->
                binding.playerMenu.visibility(visibility)
            }
        }
    }

}
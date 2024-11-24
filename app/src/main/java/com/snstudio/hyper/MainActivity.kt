package com.snstudio.hyper

import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.gone
import com.snstudio.hyper.core.extension.loadArtwork
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.slideDownToggle
import com.snstudio.hyper.core.extension.slideInUp
import com.snstudio.hyper.core.extension.slideOutDown
import com.snstudio.hyper.databinding.ActivityMainBinding
import com.snstudio.hyper.shared.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var currentAnimation: ViewPropertyAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        createMusicFolder()
        initNavController()
        setupSmoothBottomMenu()
        initNavDestinationListener()
        observeData()
        initPlayerMenuButtonsListener()
        setupBottomSheet()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaViewModel.releasePLayer()
    }

    private fun createMusicFolder() {
        viewModel.createMusicFolder()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.playerBottomSheet)

        bottomSheetBehavior.apply {
            isHideable = false
            peekHeight = 0
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(
            object :
                BottomSheetBehavior.BottomSheetCallback() {
                @OptIn(UnstableApi::class)
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                    currentAnimation?.cancel()
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        currentAnimation = binding.miniPlayerView.slideOutDown()
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        window.navigationBarColor = getColor(R.color.secondary_background_color)
                        currentAnimation = binding.miniPlayerView.slideInUp()
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                    binding.miniPlayerView.alpha = 1 - slideOffset
                }
            },
        )
    }

    @Deprecated("Deprecated in Java") // Todo
    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private fun expandBottomSheet() {
        with(binding) {
            playerBottomSheet.apply {
                visibility = View.VISIBLE
                alpha = 0f
            }

            playerBottomSheet.animate()
                .alpha(1f)
                .setDuration(200)
                .withStartAction {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }.start()

            miniPlayerView.animate()
                .translationY(-miniPlayerView.height.toFloat())
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    miniPlayerView.translationY = 0f
                    miniPlayerView.alpha = 1f
                }.start()
        }
    }

    @OptIn(UnstableApi::class)
    private fun observeData() {
        with(mediaViewModel) {
            observe(playerLiveData) { player ->
                with(binding) {
                    miniPlayerView.player = player
                    fullPlayerView.player = player
                }
            }

            observe(progressLiveData) { value ->
                binding.miniPlayerView.findViewById<ProgressBar>(R.id.progressCircular).apply {
                    progress = value
                }
            }
            observe(metaDataLiveData) { metadata ->
                with(binding) {
                    miniPlayerView.apply {
                        slideDownToggle(true)
                        findViewById<AppCompatImageView>(R.id.image).loadArtwork(metadata)
                        findViewById<MaterialTextView>(R.id.title).apply {
                            text = metadata.title
                            isSelected = true
                        }
                    }
                    fullPlayerView.apply {
                        findViewById<MaterialTextView>(R.id.title).apply {
                            text = metadata.title
                            isSelected = true
                        }
                        findViewById<AppCompatImageView>(R.id.image).loadArtwork(metadata)
                    }
                }
            }
            observe(showPlayerMenuLiveData) { visibility ->
                if (visibility) binding.miniPlayerView.show() else binding.miniPlayerView.gone()
            }
        }
    }

    private fun initNavController() {
        navController = findNavController(R.id.baseNavHost)
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_bar_menu)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
    }

    private fun initNavDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchFragment, R.id.playlistDetail -> {
                    binding.bottomBar.slideDownToggle(false)
                }

                else -> binding.bottomBar.slideDownToggle(true)
            }
        }
    }

    private fun initPlayerMenuButtonsListener() {
        binding.miniPlayerView.click {
            window.navigationBarColor = getColor(R.color.background_color)
            expandBottomSheet()
        }
    }
}

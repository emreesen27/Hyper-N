package com.snstudio.hyper.feature.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.snstudio.hyper.BuildConfig
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.isValidImageUrl
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.openUrlInBrowser
import com.snstudio.hyper.core.extension.startActivitySafely
import com.snstudio.hyper.core.extension.toMediaList
import com.snstudio.hyper.data.MediaItemBuilder
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.FragmentHomeBinding
import com.snstudio.hyper.shared.MediaViewModel
import com.snstudio.hyper.util.DATA_KEY
import com.snstudio.hyper.util.EXCEPTION
import com.snstudio.hyper.util.ErrorDialog
import com.snstudio.hyper.util.MediaItemType
import com.snstudio.hyper.util.RECEIVED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override fun getViewModelClass() = HomeViewModel::class.java

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    private lateinit var mediaViewModel: MediaViewModel
    private var skeletonHighlights: Skeleton? = null
    private var skeletonDownloads: Skeleton? = null

    private val adapterHighlights by lazy {
        HighlightsAdapter(
            onItemCLick = { media ->
                viewModel.invokeAudioUrl(media)
            },
        )
    }

    private val lastDownloadsAdapter by lazy {
        LastDownloadsAdapter(
            onAddCLick = {
                navigate(HomeFragmentDirections.goToSearch())
            },
            onItemCLick = { media, _ ->
                playMedia(media)
            },
        )
    }

    override fun observeData() {
        observe(viewModel.forceUpdateLiveData) { lastVersion ->
            lastVersion?.let {
                if (lastVersion != BuildConfig.VERSION_NAME) {
                    showVersionDialog(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.receivedData.collect { call ->
                when (call.method) {
                    RECEIVED.HIGHLIGHTS_RECEIVED.received -> {
                        call.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                            adapterHighlights.setItems(data.toMediaList(MediaItemType.REMOTE))
                            skeletonHighlights?.showOriginal()
                        }
                    }

                    RECEIVED.AUDIO_URL_RECEIVED.received -> {
                        call.argument<HashMap<String, String>>(DATA_KEY)?.let { data ->
                            val url = data["url"].orEmpty()
                            val errorCode = data["errorCode"]
                            if (!errorCode.isNullOrEmpty()) {
                                showErrorDialog(errorCode)
                                mediaViewModel.showPLayerMenu(false)
                                return@let
                            }
                            playMediaWithUrl(url)
                        }
                    }
                }
            }
        }
        observe(viewModel.localMediaListLiveData) { mediaList ->
            lastDownloadsAdapter.addItems(mediaList)
            skeletonDownloads?.showOriginal()
        }
    }

    override fun onResume() {
        super.onResume()
        initNotificationPermission()
    }

    override fun setupViews() {
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            recyclerHighlights.adapter = adapterHighlights
            recyclerDownloads.adapter = lastDownloadsAdapter
        }
        viewModel.initReceive()
        initMediaViewModel()
        initSkeletonHighlights()
        initSkeletonDownloads()
        initClickListener()
    }

    private fun initClickListener() {
        with(binding) {
            colorizedBar.setOnIconClickListener { index ->
                when (index) {
                    0 -> navigate(HomeFragmentDirections.goToSearch())
                    else -> return@setOnIconClickListener
                }
            }
        }
    }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                allowNotificationPermission()
            }
        }

    private fun initNotificationPermission() {
        if (!viewModel.notificationRuntimeRequested) {
            if (!checkNotificationPermission(requireContext())) {
                allowNotificationPermission()
            }
            viewModel.notificationRuntimeRequested = true
        }
    }

    private fun allowNotificationPermission() {
        if (!viewModel.hasNotificationRequestedPermissionBefore()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            routeNotificationSettings()
        }
        viewModel.setNotificationPermissionRequested()
    }

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun routeNotificationSettings() {
        val notificationManager = NotificationManagerCompat.from(requireContext())
        if (!notificationManager.areNotificationsEnabled()) {
            val settingsIntent =
                Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                }
            context?.startActivitySafely(settingsIntent)
        }
    }

    private fun showVersionDialog(lastVersion: String) {
        val dialog = VersionDialog()
        dialog.onClick = { choose ->
            if (choose) {
                context?.openUrlInBrowser(BuildConfig.RELEASE_DOWNLOAD.plus(lastVersion))
            } else {
                dialog.dismiss()
            }
        }
        dialog.showDialog(childFragmentManager)
    }

    private fun initSkeletonHighlights() {
        if (skeletonHighlights == null) {
            binding.recyclerHighlights.applySkeleton(R.layout.item_highlights).apply {
                skeletonHighlights = this
                maskColor =
                    ContextCompat.getColor(requireContext(), R.color.secondary_background_color)
                shimmerColor =
                    ContextCompat.getColor(requireContext(), R.color.third_background_color)
                showSkeleton()
            }
        }
    }

    private fun initSkeletonDownloads() {
        if (skeletonDownloads == null) {
            binding.recyclerDownloads.applySkeleton(R.layout.item_highlights).apply {
                skeletonDownloads = this
                maskColor =
                    ContextCompat.getColor(requireContext(), R.color.secondary_background_color)
                shimmerColor =
                    ContextCompat.getColor(requireContext(), R.color.third_background_color)
                showSkeleton()
            }
        }
    }

    private fun playMedia(media: Media) {
        media.localPath?.let {
            val item =
                MediaItemBuilder().setMediaId(it)
                    .setArtWorkBitmap(media.bitmap)
                    .setMediaTitle(media.title)
                    .build()
            with(mediaViewModel) {
                showPLayerMenu(true)
                playMediaItem(item)
            }
        }
    }

    private fun playMediaWithUrl(url: String) {
        with(viewModel) {
            currentMedia?.let { media ->
                media.thumbnailMax.isValidImageUrl(requireContext()) { isValid ->
                    val thumb = if (isValid) media.thumbnailMax else media.thumbnail
                    val item =
                        MediaItemBuilder().setMediaId(url)
                            .setArtWorkUrl(thumb.orEmpty())
                            .setMediaTitle(media.title)
                            .build()
                    mediaViewModel.playMediaItem(item)
                }
            }
        }
    }

    private fun showErrorDialog(code: String) {
        val errMessage =
            if (code == EXCEPTION.YT_EXPLODE_EXCEPTION.code) {
                R.string.yt_explode_error
            } else {
                R.string.unexpected_error
            }
        ErrorDialog(errMessage = errMessage).showDialog(childFragmentManager)
    }
}

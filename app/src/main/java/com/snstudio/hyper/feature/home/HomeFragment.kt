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
import com.snstudio.hyper.BuildConfig
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.openUrlInBrowser
import com.snstudio.hyper.core.extension.startActivitySafely
import com.snstudio.hyper.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override fun getViewModelClass() = HomeViewModel::class.java

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun observeData() {
        observe(viewModel.forceUpdateLiveData) { update ->
            if (update) {
                // showVersionDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initNotificationPermission()
    }

    override fun setupViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initClickListener()
    }

    private fun initClickListener() {
        with(binding) {
            musicCard.root.click {
                navigate(HomeFragmentDirections.goToLibrary())
            }
            playList.root.click {
                navigate(HomeFragmentDirections.goToPlaylist())
            }
            colorizedBar.setOnIconClickListener { index ->
                when (index) {
                    0 -> navigate(HomeFragmentDirections.goToSearch())
                    else -> return@setOnIconClickListener
                }
            }
        }
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

    private fun showVersionDialog() {
        val dialog = VersionDialog()
        dialog.onClick = { choose ->
            if (choose) {
                context?.openUrlInBrowser(BuildConfig.RELEASE_VERSION)
            } else {
                dialog.dismiss()
                activity?.finish()
            }
        }
        dialog.showDialog(childFragmentManager)
    }
}

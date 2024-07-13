package com.snstudio.hyper.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.click
import com.snstudio.hyper.core.extension.startActivitySafely
import com.snstudio.hyper.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun getViewModelClass() = HomeViewModel::class.java

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun observeData() {}

    override fun onResume() {
        super.onResume()
        initNotificationPermission()
    }

    override fun setupViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initMenuClick()
    }

    private fun initMenuClick() {
        with(binding) {
            search.click {
                navigate(HomeFragmentDirections.goToSearch())
            }
            musicCard.root.click {
                navigate(HomeFragmentDirections.goToLibrary())
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

}

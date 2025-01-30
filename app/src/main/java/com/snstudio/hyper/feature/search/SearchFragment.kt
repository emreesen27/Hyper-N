package com.snstudio.hyper.feature.search

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.addOnScrolledToEnd
import com.snstudio.hyper.core.extension.convertToBitmap
import com.snstudio.hyper.core.extension.infoToast
import com.snstudio.hyper.core.extension.isValidImageUrl
import com.snstudio.hyper.core.extension.restoreScrollPosition
import com.snstudio.hyper.core.extension.toMediaList
import com.snstudio.hyper.core.extension.waningToast
import com.snstudio.hyper.data.MediaItemBuilder
import com.snstudio.hyper.data.model.Media
import com.snstudio.hyper.databinding.FragmentSearchBinding
import com.snstudio.hyper.service.JobCompletedCallback
import com.snstudio.hyper.service.JobService
import com.snstudio.hyper.shared.MediaItemAdapter
import com.snstudio.hyper.shared.MediaViewModel
import com.snstudio.hyper.shared.ProgressLiveData
import com.snstudio.hyper.util.DATA_KEY
import com.snstudio.hyper.util.EXCEPTION
import com.snstudio.hyper.util.ErrorDialog
import com.snstudio.hyper.util.MediaItemType
import com.snstudio.hyper.util.RECEIVED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(),
    JobCompletedCallback {
    private lateinit var mediaViewModel: MediaViewModel
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    private val mediaItemAdapter by lazy {
        MediaItemAdapter(onItemCLick = { media, _ ->
            mediaViewModel.showPLayerMenu(true)
            viewModel.invokeAudioUrl(media, SearchViewModel.AudioActionType.PLAY)
        }, onMenuClick = { media, view ->
            showPopupMenu(media, view)
        })
    }

    override fun getViewModelClass() = SearchViewModel::class.java

    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

    override fun setupViews() {
        initMediaViewModel()
        initMediaRecycler()
        initSearch()
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun observeData() {
        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                receivedData.collect {
                    when (it.method) {
                        RECEIVED.SEARCH_RECEIVED.received -> {
                            it.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                                viewModel.searchProgressObservable.set(false)
                                viewModel.searchResultIsEmptyObservable.set(data.isEmpty())
                                mediaItemAdapter.setItems(data.toMediaList(MediaItemType.REMOTE))
                            }
                        }

                        RECEIVED.AUDIO_URL_RECEIVED.received -> {
                            it.argument<HashMap<String, String>>(DATA_KEY)?.let { data ->
                                val url = data["url"].orEmpty()
                                val errorCode = data["errorCode"]
                                if (!errorCode.isNullOrEmpty()) {
                                    showErrorDialog(errorCode)
                                    mediaViewModel.showPLayerMenu(false)
                                    return@collect
                                }
                                when (audioActionType) {
                                    SearchViewModel.AudioActionType.PLAY -> {
                                        playMedia(url)
                                    }

                                    SearchViewModel.AudioActionType.DOWNLOAD -> {
                                        startDownloadService(url)
                                    }
                                }
                            }
                        }

                        RECEIVED.NEXT_RECEIVED.received -> {
                            it.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                                mediaItemAdapter.addItem(data.toMediaList(MediaItemType.REMOTE))
                                binding.recyclerMedia.restoreScrollPosition()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onJobStart(id: String) {
        ProgressLiveData.updateDownloadState(
            ProgressLiveData.DownloadState.Started(id, viewModel.currentMedia?.title.orEmpty()),
        )
    }

    override fun onJobProgress(
        media: Media,
        progress: Int,
    ) {
        ProgressLiveData.updateDownloadState(
            ProgressLiveData.DownloadState.InProgress(
                media,
                progress,
            ),
        )
    }

    override fun onJobCompleted(media: Media) {
        ProgressLiveData.updateDownloadState(ProgressLiveData.DownloadState.Completed(media.id))
        val thumb = media.thumbnailMax ?: media.thumbnail
        thumb?.convertToBitmap(requireContext(), onSuccess = { bitmap ->
            val localMedia = media.copy(bitmap = bitmap, type = MediaItemType.LOCAL.key)
            viewModel.insertMediaJob(localMedia)
        }, onFailure = {})
    }

    private fun playMedia(url: String) {
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

    private fun initSearch() {
        with(binding.searchView) {
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                .apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setTextCursorDrawable(R.drawable.cursor_color)
                    }
                    setHintTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_color,
                        ),
                    )
                }
            setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        logSearchEvent(query)
                        viewModel.invokeSearch(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        return true
                    }
                },
            )
        }
    }

    private fun initMediaRecycler() {
        with(binding.recyclerMedia) {
            adapter = mediaItemAdapter
            itemAnimator = null
            addOnScrolledToEnd { viewModel.invokeNext() }
        }
    }

    private fun getAudioUrlForDownload(media: Media) {
        if (viewModel.isMediaSavedLocally(media)) {
            context?.infoToast(getString(R.string.already_in_lib))
            return
        }

        if (JobService.runningJobCount >= 3) {
            context?.waningToast(getString(R.string.please_wait_download))
            return
        }

        viewModel.invokeAudioUrl(media, SearchViewModel.AudioActionType.DOWNLOAD)
    }

    private fun startDownloadService(url: String) {
        viewModel.currentMedia?.let { media ->
            JobService.download(
                media,
                url,
                this,
                requireContext(),
            )
        }
    }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
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

    private fun showPopupMenu(
        media: Media,
        view: View,
    ) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.PopupMenuTheme), view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }
        popupMenu.menuInflater.inflate(R.menu.search_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.download -> {
                    getAudioUrlForDownload(media)
                    true
                }

                R.id.play -> {
                    mediaViewModel.showPLayerMenu(true)
                    viewModel.invokeAudioUrl(media, SearchViewModel.AudioActionType.PLAY)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    fun logSearchEvent(query: String) {
        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
            }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }
}

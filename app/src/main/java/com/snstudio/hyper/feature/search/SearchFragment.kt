package com.snstudio.hyper.feature.search

import android.annotation.SuppressLint
import android.os.Build
import android.util.TypedValue
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.snstudio.hyper.R
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.addOnScrolledToEnd
import com.snstudio.hyper.core.extension.convertToBitmap
import com.snstudio.hyper.core.extension.infoToast
import com.snstudio.hyper.core.extension.observe
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
import com.snstudio.hyper.util.ItemTouchHelperCallback
import com.snstudio.hyper.util.MediaItemType
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.util.SwipeAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(),
    JobCompletedCallback {
    private lateinit var mediaViewModel: MediaViewModel

    private val mediaItemAdapter by lazy {
        MediaItemAdapter(onClick = { media ->
            viewModel.invokeAudioUrl(media, SearchViewModel.AudioActionType.PLAY)
        })
    }

    override fun getViewModelClass() = SearchViewModel::class.java

    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

    override fun setupViews() {
        initMediaViewModel()
        setDataBinding()
        initMediaRecycler()
        attachItemTouchHelperCallback()
        initSearch()
    }

    override fun observeData() {
        with(viewModel) {
            observe(receivedData) {
                when (it.method) {
                    RECEIVED.SEARCH_RECEIVED.received -> {
                        it.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                            viewModel.searchProgressObservable.set(false)
                            viewModel.searchResultIsEmptyObservable.set(data.isEmpty())
                            mediaItemAdapter.setItems(data.toMediaList(MediaItemType.SEARCH))
                        }
                    }

                    RECEIVED.AUDIO_URL_RECEIVED.received -> {
                        it.argument<String>(DATA_KEY)?.let { url ->
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
                            mediaItemAdapter.addItem(data.toMediaList(MediaItemType.SEARCH))
                            binding.recyclerMedia.restoreScrollPosition()
                        }
                    }
                }
            }
        }
    }

    override fun onJobStart(id: String) {
        ProgressLiveData.updateDownloadState(
            ProgressLiveData.DownloadState.Started(
                viewModel.currentMedia?.title.orEmpty(),
            ),
        )
    }

    override fun onJobProgress(progress: Int) {
        ProgressLiveData.updateDownloadState(
            ProgressLiveData.DownloadState.InProgress(
                progress,
            ),
        )
    }

    override fun onJobCompleted(media: Media) {
        ProgressLiveData.updateDownloadState(ProgressLiveData.DownloadState.Completed)
        media.thumbnail?.convertToBitmap(requireContext(), onSuccess = { bitmap ->
            val localMedia = media.copy(bitmap = bitmap, type = MediaItemType.LOCAL.key)
            viewModel.insertMediaJob(localMedia)
        }, onFailure = {})
    }

    private fun playMedia(url: String) {
        with(viewModel) {
            currentMedia?.let { media ->
                val item =
                    MediaItemBuilder().setMediaId(url)
                        .setMediaTitle(media.title)
                        .build()
                mediaViewModel.playMediaItem(item)
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
            // addDivider(requireContext())
            addOnScrolledToEnd { viewModel.invokeNext() }
        }
    }

    @SuppressLint("NotifyDataSetChanged") // Todo
    private fun attachItemTouchHelperCallback() {
        val callback =
            ItemTouchHelperCallback(
                requireContext(),
                swipeAction = SwipeAction.DOWNLOAD,
                onSwipedCallback = { pos ->
                    mediaItemAdapter.notifyDataSetChanged()
                    getAudioUrlForDownload(mediaItemAdapter.mediaItems[pos])
                },
            )
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerMedia)
    }

    private fun getAudioUrlForDownload(media: Media) {
        if (viewModel.isMediaSavedLocally(media)) {
            context?.infoToast(getString(R.string.already_in_lib))
            return
        }

        if (JobService.runningJobCount > 0) {
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

    private fun setDataBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}

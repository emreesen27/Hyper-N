package com.snstudio.hyper.ui.search

import android.net.Uri
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.snstudio.hyper.util.DATA_KEY
import com.snstudio.hyper.ui.shared.MediaItemAdapter
import com.snstudio.hyper.ui.shared.MediaViewModel
import com.snstudio.hyper.util.RECEIVED
import com.snstudio.hyper.core.base.BaseFragment
import com.snstudio.hyper.core.extension.addDivider
import com.snstudio.hyper.core.extension.addOnScrolledToEnd
import com.snstudio.hyper.core.extension.observe
import com.snstudio.hyper.core.extension.toMediaList
import com.snstudio.hyper.data.Media
import com.snstudio.hyper.databinding.FragmentSearchBinding
import com.snstudio.hyper.service.JobCompletedCallback
import com.snstudio.hyper.service.JobService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(),
    JobCompletedCallback {
    private lateinit var mediaViewModel: MediaViewModel
    private var mediaItemAdapter: MediaItemAdapter? = null
    override fun getViewModelClass() = SearchViewModel::class.java
    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

    override fun onJobProgress(progress: Int) {
    }

    override fun onJobCompleted() {}

    override fun setupViews() {
        initMediaViewModel()
        initAdapter()
        initSearch()
    }

    override fun observeData() {
        with(viewModel) {
            observe(receivedData) {
                when (it.method) {
                    RECEIVED.SEARCH_RECEIVED.received -> {
                        it.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                            mediaItemAdapter?.setItems(data.toMediaList(1))
                        }
                    }

                    RECEIVED.AUDIO_URL_RECEIVED.received -> {
                        it.argument<String>(DATA_KEY)?.let { url ->
                            viewModel.selectedMedia?.let { media ->
                                //startDownloadService(media, url)
                                //(activity as MainActivity).playMedia(url)
                                mediaViewModel.playMediaItem(Uri.parse(url))
                            }
                        }
                    }

                    RECEIVED.NEXT_RECEIVED.received -> {
                        it.argument<List<HashMap<String, String>>>(DATA_KEY)?.let { data ->
                            mediaItemAdapter?.addItem(data.toMediaList(1))
                        }
                    }

                }
            }
        }
    }

    private fun startDownloadService(
        media: Media,
        url: String,
    ) {
        JobService.download(
            media,
            url,
            this@SearchFragment,
            requireContext(),
        )
    }

    private fun initSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.search(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    private fun initAdapter() {
        if (mediaItemAdapter == null) {
            mediaItemAdapter = MediaItemAdapter(onClick = { media ->
                with(viewModel) {
                    selectedMedia = media
                    getAudioUrl(media.id)
                }
            })
        }
        with(binding.recyclerMedia) {
            adapter = mediaItemAdapter
            addDivider(requireContext())
            addOnScrolledToEnd { viewModel.next() }
        }
    }

    private fun initMediaViewModel() {
        mediaViewModel = ViewModelProvider(requireActivity())[MediaViewModel::class.java]
    }

}
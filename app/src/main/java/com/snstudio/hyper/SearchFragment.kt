package com.snstudio.hyper

import com.snstudio.hyper.core.BaseFragment
import com.snstudio.hyper.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    private var adapter: MediaAdapter? = null

    override fun getViewModelClass() = SearchViewModel::class.java

    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

    override fun observeData() {
        viewModel.receivedData.observe(viewLifecycleOwner) {
            if (it.method == MethodName.SEARCH_RECEIVED) {
                val data = it.argument<List<HashMap<String, String>>>(MethodName.DATA)
                val map = data?.map { hashMap ->
                    Media(
                        id = hashMap["id"] ?: "",
                        title = hashMap["title"] ?: "",
                        description = hashMap["description"] ?: "",
                        author = hashMap["author"] ?: "",
                        url = hashMap["url"] ?: "",
                        duration = hashMap["duration"] ?: "",
                        thumbnails = hashMap["thumbnails"] ?: "",
                    )
                }?.toMutableList()
                if (map != null) {
                    adapter?.setItems(map)
                }
            }
            viewModel.nextPage()
        }
    }

    override fun setupViews() {
        initAdapter()
    }


    private fun initAdapter() {
        if (adapter == null) {
            adapter = MediaAdapter()
        }
        binding.RecyclerMedia.adapter = adapter
    }


}

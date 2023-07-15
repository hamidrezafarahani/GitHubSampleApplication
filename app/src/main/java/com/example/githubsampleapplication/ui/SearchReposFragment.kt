package com.example.githubsampleapplication.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsampleapplication.R
import com.example.githubsampleapplication.databinding.FragmentSearchReposBinding
import com.example.githubsampleapplication.utils.log
import com.example.githubsampleapplication.binding.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchReposFragment : Fragment(R.layout.fragment_search_repos) {

    private val binding by viewBindings {
        FragmentSearchReposBinding.bind(it)
    }

    private val viewModel by viewModels<SearchReposViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            repoList.addItemDecoration(itemDecoration)

            val repoAdapter = ReposAdapter()
            val stateAdapter = ReposLoadStateAdapter { repoAdapter.retry() }
            repoList.adapter = repoAdapter.withLoadStateHeaderAndFooter(
                header = stateAdapter,
                footer = stateAdapter
            )

            bindSearch(
                uiState = viewModel.state,
                onQueryChanged = viewModel.accept
            )

            bindList(
                stateAdapter = stateAdapter,
                repoAdapter = repoAdapter,
                uiState = viewModel.state,
                pagingData = viewModel.pagingDataFlow,
                onScrollChanged = viewModel.accept
            )
        }
    }

    private fun FragmentSearchReposBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchRepo.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchRepo.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uiState.map {
                    it.query
                }.distinctUntilChanged().collect {
                    searchRepo.setText(it)
                }
            }
        }
    }

    private fun FragmentSearchReposBinding.updateRepoListFromInput(
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                repoList.scrollToPosition(0)
                onQueryChanged(UiAction.Search(it.toString()))
            }
        }
    }

    private fun FragmentSearchReposBinding.bindList(
        stateAdapter: ReposLoadStateAdapter,
        repoAdapter: ReposAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {

        retryButton.setOnClickListener { repoAdapter.retry() }
        repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
                }
            }
        })

        val notLoading = repoAdapter.loadStateFlow
            .asPresentationState()
            .map {
                it == PresentationState.PRESENTED
            }
        val hasNotScrolledForCurrentSearch = uiState.map {
            it.hasNotScrolledForCurrentSearch
        }.distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading, hasNotScrolledForCurrentSearch, Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagingData.collectLatest {
                    repoAdapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shouldScrollToTop.collect {
                    if (it) repoList.scrollToPosition(0)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repoAdapter.loadStateFlow.collect { loadState ->

                    stateAdapter.loadState = loadState.mediator?.refresh?.takeIf {
                        it is LoadState.Error && repoAdapter.itemCount > 0
                    } ?: loadState.prepend

                    val isListEmpty = loadState.refresh is LoadState.NotLoading
                            && repoAdapter.itemCount == 0
                    emptyList.isVisible = isListEmpty

                    repoList.isVisible = loadState.source.refresh is LoadState.NotLoading
                            || loadState.mediator?.refresh is LoadState.NotLoading

                    progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading

                    retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error
                            && repoAdapter.itemCount == 0

                    val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.error?.let {
                        requireContext() log it
                    }
                }
            }
        }

    }

}
package com.example.githubsampleapplication.ui

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.scan

enum class PresentationState {
    INITIAL, REMOTE_LOADING, SOURCE_LOADING, PRESENTED
}

fun Flow<CombinedLoadStates>.asPresentationState(): Flow<PresentationState> {
    return scan(PresentationState.INITIAL) { accumulator: PresentationState, value: CombinedLoadStates ->
        when (accumulator) {
            PresentationState.INITIAL, PresentationState.PRESENTED -> {
                when (value.mediator?.refresh) {
                    is LoadState.Loading -> PresentationState.REMOTE_LOADING
                    else -> accumulator
                }
            }

            PresentationState.REMOTE_LOADING -> {
                when (value.source.refresh) {
                    is LoadState.Loading -> PresentationState.SOURCE_LOADING
                    else -> accumulator
                }
            }

            PresentationState.SOURCE_LOADING -> {
                when (value.source.refresh) {
                    is LoadState.NotLoading -> PresentationState.PRESENTED
                    else -> accumulator
                }
            }
        }
    }.distinctUntilChanged()
}
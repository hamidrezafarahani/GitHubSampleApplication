package com.example.githubsampleapplication.ui

import com.example.githubsampleapplication.data.local.Repo

const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
const val LAST_SEARCH_QUERY: String = "last_search_query"
const val DEFAULT_QUERY = "Android"

val UiModel.RepoItem.roundedStarCount: Int
    get() = this.repo.stars / 10_000

sealed class UiModel {

    data class RepoItem(val repo: Repo) : UiModel()

    data class SeparatorItem(val description: String) : UiModel()
}

sealed class UiAction {

    data class Search(val query: String) : UiAction()

    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)
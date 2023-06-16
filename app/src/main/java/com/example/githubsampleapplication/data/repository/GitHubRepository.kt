package com.example.githubsampleapplication.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubsampleapplication.data.local.DB
import com.example.githubsampleapplication.data.local.Repo
import com.example.githubsampleapplication.data.remote.GitHubService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GitHubRepository @Inject constructor(
    private val db: DB,
    private val service: GitHubService
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getSearchResult(query: String): Flow<PagingData<Repo>> {
        val config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false)
        val remoteMediator = GitHubRemoteMediator(query, db, service)
        val pagingSourceFactory = { db.repoDao().reposByName(query) }

        return Pager(config = config, remoteMediator = remoteMediator) {
            pagingSourceFactory()
        }.flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}
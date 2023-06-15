package com.example.githubsampleapplication.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.githubsampleapplication.data.local.DB
import com.example.githubsampleapplication.data.local.Repo
import com.example.githubsampleapplication.data.remote.GitHubService

@OptIn(ExperimentalPagingApi::class)
class GitHubRemoteMediator(
    private val query: String,
    private val db: DB,
    private val service: GitHubService
) : RemoteMediator<Int, Repo>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Repo>
    ): MediatorResult {

        TODO("paging logic")
    }
}
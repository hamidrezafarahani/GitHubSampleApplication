package com.example.githubsampleapplication.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.githubsampleapplication.data.local.DB
import com.example.githubsampleapplication.data.local.RemoteKey
import com.example.githubsampleapplication.data.local.RemoteKeyDao
import com.example.githubsampleapplication.data.local.Repo
import com.example.githubsampleapplication.data.remote.GitHubService
import timber.log.Timber

private const val GITHUB_STARTING_PAGE_INDEX = 1

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

        val page = when (loadType) {
            LoadType.PREPEND -> {
                val remoteKey = state getFirstItemIn db.remoteDao()
                val prevKey = remoteKey?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKey = state getLastItemIn db.remoteDao()
                val nextKey = remoteKey?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                nextKey
            }

            LoadType.REFRESH -> {
                val remoteKey = state getClosestToCurrentPositionIn db.remoteDao()
                remoteKey?.nextKey?.minus(1) ?: GITHUB_STARTING_PAGE_INDEX
            }
        }

        return try {
            val response = service.searchRepos(query, page, state.config.pageSize)
            val repos = response.items
            val endOfPaginationReached = repos.isEmpty()
            with(db) {
                withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        repoDao().clearRepos()
                        remoteDao().clearRemoteKeys()
                    }
                    val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = repos.map {
                        RemoteKey(it.id, prevKey, nextKey)
                    }

                    repoDao().insertAll(repos)
                    remoteDao().insertAll(keys)
                }
            }
            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            Timber.tag("mediator-paging").d(e)
            MediatorResult.Error(e)
        }
    }
}

private suspend infix fun PagingState<Int, Repo>.getLastItemIn(
    dao: RemoteKeyDao
): RemoteKey? {
    return pages.lastOrNull {
        it.data.isNotEmpty()
    }?.data?.lastOrNull()?.let {
        dao.remoteKeyByRepoId(it.id)
    }
}

private suspend infix fun PagingState<Int, Repo>.getFirstItemIn(
    dao: RemoteKeyDao
): RemoteKey? {
    return pages.firstOrNull {
        it.data.isNotEmpty()
    }?.data?.firstOrNull()?.let {
        dao.remoteKeyByRepoId(it.id)
    }
}

private suspend infix fun PagingState<Int, Repo>.getClosestToCurrentPositionIn(
    dao: RemoteKeyDao
): RemoteKey? {
    return anchorPosition?.let { position ->
        closestItemToPosition(position)?.let { repo ->
            dao.remoteKeyByRepoId(repo.id)
        }
    }
}
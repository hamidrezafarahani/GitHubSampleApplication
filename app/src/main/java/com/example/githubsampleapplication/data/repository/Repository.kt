package com.example.githubsampleapplication.data.repository

import androidx.paging.PagingData
import com.example.githubsampleapplication.data.local.entities.Repo
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getSearchResult(query: String): Flow<PagingData<Repo>>
}
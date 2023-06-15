package com.example.githubsampleapplication.data.remote

import com.example.githubsampleapplication.data.local.Repo
import com.google.gson.annotations.SerializedName as SN

data class RepoSearchResponse(
    @SN("total_count") val total: Int = 0,
    @SN("items") val items: List<Repo> = emptyList()
)
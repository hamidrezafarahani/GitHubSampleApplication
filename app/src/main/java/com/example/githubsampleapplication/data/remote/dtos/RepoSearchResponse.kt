package com.example.githubsampleapplication.data.remote.dtos

import com.example.githubsampleapplication.data.local.entities.Repo
import com.google.gson.annotations.SerializedName as SN

data class RepoSearchResponse(
    @SN("total_count") val total: Int = 0,
    @SN("items") val items: List<Repo> = emptyList()
)
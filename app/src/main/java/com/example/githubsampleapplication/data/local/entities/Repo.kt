package com.example.githubsampleapplication.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey as PK
import com.google.gson.annotations.SerializedName as SN

@Entity(tableName = "repos")
data class Repo(
    @SN("id") @PK val id: Long,
    @SN("name") val name: String,
    @SN("full_name") val fullName: String,
    @SN("description") val description: String?,
    @SN("html_url") val url: String,
    @SN("stargazers_count") val stars: Int,
    @SN("forks_count") val forks: Int,
    @SN("language") val language: String?
)
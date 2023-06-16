package com.example.githubsampleapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey as PK

@Entity(tableName = "remote_key")
data class RemoteKey(
    @PK val repoId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
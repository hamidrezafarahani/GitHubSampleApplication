package com.example.githubsampleapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.githubsampleapplication.data.local.dao.RemoteKeyDao
import com.example.githubsampleapplication.data.local.dao.RepoDao
import com.example.githubsampleapplication.data.local.entities.RemoteKey
import com.example.githubsampleapplication.data.local.entities.Repo

@Database(
    entities = [Repo::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class DB : RoomDatabase() {

    abstract fun repoDao(): RepoDao

    abstract fun remoteDao(): RemoteKeyDao
}
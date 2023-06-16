package com.example.githubsampleapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Repo::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class DB : RoomDatabase() {

    abstract fun repoDao(): RepoDao

    abstract fun remoteDao(): RemoteKeyDao
}
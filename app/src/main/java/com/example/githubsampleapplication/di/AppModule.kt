package com.example.githubsampleapplication.di

import android.content.Context
import androidx.room.Room
import com.example.githubsampleapplication.data.local.DB
import com.example.githubsampleapplication.data.local.RepoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDB(@ApplicationContext context: Context): DB {
        return Room.databaseBuilder(
            context,
            DB::class.java,
            "github-app.database"
        ).build()
    }

    @Provides
    fun provideRepoDao(db: DB): RepoDao {
        return db.repoDao()
    }
}
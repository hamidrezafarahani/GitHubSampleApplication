package com.example.githubsampleapplication.di

import com.example.githubsampleapplication.data.repository.GitHubRepository
import com.example.githubsampleapplication.data.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BinderModule {

    @Binds
    abstract fun bindGitHubRepository(repo: GitHubRepository): Repository
}
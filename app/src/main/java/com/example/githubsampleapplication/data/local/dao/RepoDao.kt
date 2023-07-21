package com.example.githubsampleapplication.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubsampleapplication.data.local.entities.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Repo>)

    @Query("DELETE FROM repos")
    suspend fun clearRepos()

    @Query(
        """
        SELECT * FROM repos WHERE name LIKE :queryString OR description LIKE :queryString
        ORDER BY stars DESC, name ASC
    """
    )
    fun reposByName(queryString: String): PagingSource<Int, Repo>
}
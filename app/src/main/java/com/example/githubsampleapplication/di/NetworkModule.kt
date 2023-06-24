package com.example.githubsampleapplication.di

import android.app.Application
import com.example.githubsampleapplication.data.remote.GitHubService
import com.facebook.shimmer.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @LoggingInterceptor
    fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor {
            Timber.tag("log-okhttp").d(it)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @AuthInterceptor
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor {
            val url = it.request().url.newBuilder().apply {
                /* your logic */
            }.build()
            val request = it.request().newBuilder()
                .url(url)
                /*.addHeader("Authorization", "Bearer ....")*/
                .build()
            it.proceed(request)
        }
    }

    @Provides
    fun provideOkHttpCache(app: Application): Cache {
        return Cache(app.cacheDir, 50_000_000)
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        @AuthInterceptor authInterceptor: Interceptor,
        @LoggingInterceptor loggingInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideRetrofit(
        client: dagger.Lazy<OkHttpClient>,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .callFactory {
                client.get().newCall(it)
            }
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubService(retrofit: Retrofit): GitHubService {
        return retrofit.create(GitHubService::class.java)
    }
}
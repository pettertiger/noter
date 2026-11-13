package com.noter.di

import android.content.Context
import com.noter.auth.TokenManager
import com.noter.repository.DropboxRepository
import com.noter.repository.PdfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideDropboxRepository(
        @ApplicationContext context: Context,
        tokenManager: TokenManager
    ): DropboxRepository {
        return DropboxRepository(context, tokenManager)
    }

    @Provides
    @Singleton
    fun providePdfRepository(
        @ApplicationContext context: Context
    ): PdfRepository {
        return PdfRepository(context)
    }
}

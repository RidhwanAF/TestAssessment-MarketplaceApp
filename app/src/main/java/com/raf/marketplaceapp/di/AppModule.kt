package com.raf.marketplaceapp.di

import com.raf.auth.data.local.AuthDataStore
import com.raf.auth.data.remote.AuthApiService
import com.raf.auth.data.repository.AuthRepositoryImpl
import com.raf.core.domain.contract.AppSettingsProvider
import com.raf.core.domain.contract.AuthProvider
import com.raf.core.domain.usecase.GetAppSettingsUseCase
import com.raf.core.domain.usecase.GetAuthTokenUseCase
import com.raf.core.domain.usecase.LogoutUseCase
import com.raf.marketplaceapp.BuildConfig
import com.raf.settings.data.local.AppSettingsDataStore
import com.raf.settings.data.repository.AppSettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Remote
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Contracts
     */
    @Provides
    @Singleton
    fun provideAppSettingsProvider(appSettingsDataStore: AppSettingsDataStore): AppSettingsProvider {
        return AppSettingsRepositoryImpl(appSettingsDataStore)
    }

    @Provides
    @Singleton
    fun provideAuthProvider(
        authApiService: AuthApiService,
        authDataStore: AuthDataStore,
    ): AuthProvider {
        return AuthRepositoryImpl(authApiService, authDataStore)
    }

    /**
     * Use Cases
     */
    @Provides
    @Singleton
    fun provideGetAppSettingsUseCase(appSettingsProvider: AppSettingsProvider): GetAppSettingsUseCase {
        return GetAppSettingsUseCase(appSettingsProvider)
    }

    @Provides
    @Singleton
    fun provideGetAuthTokenUseCase(authProvider: AuthProvider): GetAuthTokenUseCase {
        return GetAuthTokenUseCase(authProvider)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(authProvider: AuthProvider): LogoutUseCase {
        return LogoutUseCase(authProvider)
    }
}
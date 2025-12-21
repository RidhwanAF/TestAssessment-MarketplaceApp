package com.raf.marketplace.data.di

import android.content.Context
import androidx.room.Room
import com.raf.marketplace.data.local.room.MarketplaceDatabase
import com.raf.marketplace.data.remote.MarketplaceApiService
import com.raf.marketplace.data.repository.MarketplaceRepositoryImpl
import com.raf.marketplace.domain.repository.MarketplaceRepository
import com.raf.marketplace.domain.usecase.FetchProductsUseCase
import com.raf.marketplace.domain.usecase.GetProductsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarketPlaceModule {

    @Provides
    @Singleton
    fun provideMarketplaceDatabase(
        @ApplicationContext context: Context,
    ): MarketplaceDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = MarketplaceDatabase::class.java,
            name = "marketplace_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMarketplaceApiService(retrofit: Retrofit): MarketplaceApiService {
        return retrofit.create(MarketplaceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMarketplaceRepository(
        @ApplicationContext context: Context,
        apiService: MarketplaceApiService,
        database: MarketplaceDatabase,
    ): MarketplaceRepository {
        return MarketplaceRepositoryImpl(context, apiService, database)
    }

    @Provides
    @Singleton
    fun provideFetchProductsUseCase(marketplaceRepository: MarketplaceRepository): FetchProductsUseCase {
        return FetchProductsUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideGetProductsUseCase(marketplaceRepository: MarketplaceRepository): GetProductsUseCase {
        return GetProductsUseCase(marketplaceRepository)
    }

}
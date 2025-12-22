package com.raf.marketplace.data.di

import android.content.Context
import androidx.room.Room
import com.raf.marketplace.data.local.room.MarketplaceDatabase
import com.raf.marketplace.data.remote.MarketplaceApiService
import com.raf.marketplace.data.repository.MarketplaceRepositoryImpl
import com.raf.marketplace.domain.repository.MarketplaceRepository
import com.raf.marketplace.domain.usecase.cart.AddToCartUseCase
import com.raf.marketplace.domain.usecase.cart.DeleteAllItemFromCartUseCase
import com.raf.marketplace.domain.usecase.cart.DeleteItemCartByProductIdUseCase
import com.raf.marketplace.domain.usecase.cart.GetAllItemFromCartUseCase
import com.raf.marketplace.domain.usecase.cart.GetItemCountFromCartUseCase
import com.raf.marketplace.domain.usecase.cart.UpdateQuantityByProductIdUseCase
import com.raf.marketplace.domain.usecase.product.FetchProductByIdUseCase
import com.raf.marketplace.domain.usecase.product.FetchProductsUseCase
import com.raf.marketplace.domain.usecase.product.GetProductCategoriesUseCase
import com.raf.marketplace.domain.usecase.product.GetProductsUseCase
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

    @Provides
    @Singleton
    fun provideGetProductCategoriesUseCase(marketplaceRepository: MarketplaceRepository): GetProductCategoriesUseCase {
        return GetProductCategoriesUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideFetchProductByIdUseCase(marketplaceRepository: MarketplaceRepository): FetchProductByIdUseCase {
        return FetchProductByIdUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideAddToCartUseCase(marketplaceRepository: MarketplaceRepository): AddToCartUseCase {
        return AddToCartUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteAllItemFromCartUseCase(marketplaceRepository: MarketplaceRepository): DeleteAllItemFromCartUseCase {
        return DeleteAllItemFromCartUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteItemCartByProductIdUseCase(marketplaceRepository: MarketplaceRepository): DeleteItemCartByProductIdUseCase {
        return DeleteItemCartByProductIdUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllItemFromCartUseCase(marketplaceRepository: MarketplaceRepository): GetAllItemFromCartUseCase {
        return GetAllItemFromCartUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideGetItemCountFromCartUseCase(marketplaceRepository: MarketplaceRepository): GetItemCountFromCartUseCase {
        return GetItemCountFromCartUseCase(marketplaceRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateQuantityByProductIdUseCase(marketplaceRepository: MarketplaceRepository): UpdateQuantityByProductIdUseCase {
        return UpdateQuantityByProductIdUseCase(marketplaceRepository)
    }
}
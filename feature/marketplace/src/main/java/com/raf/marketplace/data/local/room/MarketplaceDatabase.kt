package com.raf.marketplace.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.raf.marketplace.data.local.room.converter.ProductTypeConverter
import com.raf.marketplace.data.local.room.product.ProductDao
import com.raf.marketplace.data.local.room.product.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ProductTypeConverter::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract val productDao: ProductDao
}
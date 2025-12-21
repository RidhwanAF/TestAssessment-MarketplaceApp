package com.raf.marketplace.data.local.room.converter

import androidx.room.TypeConverter
import com.raf.marketplace.data.local.room.product.RatingEntity
import kotlinx.serialization.json.Json

class ProductTypeConverter {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @TypeConverter
    fun fromRatingEntity(rating: RatingEntity?): String? {
        if (rating == null) {
            return null
        }
        return json.encodeToString(rating)
    }

    @TypeConverter
    fun toRatingEntity(jsonString: String?): RatingEntity? {
        if (jsonString == null) {
            return null
        }
        return json.decodeFromString(jsonString)
    }
}
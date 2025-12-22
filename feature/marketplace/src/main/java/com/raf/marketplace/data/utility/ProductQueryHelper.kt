package com.raf.marketplace.data.utility

import androidx.sqlite.db.SimpleSQLiteQuery
import com.raf.marketplace.domain.model.ProductSortType

object ProductQueryHelper {

    fun createFilteredQuery(
        query: String,
        categories: List<String>,
        sortType: Pair<ProductSortType, Boolean>?,
    ): SimpleSQLiteQuery {
        val sortClause = if (sortType != null) {
            val sortExpressions = sortType.let { (sortType, isAsc) ->
                val sortOrder = if (isAsc) "ASC" else "DESC"
                when (sortType) {
                    ProductSortType.NAME -> "title $sortOrder"
                    ProductSortType.PRICE -> "price $sortOrder"
                    ProductSortType.RATING -> "rating $sortOrder"
                }
            }
            "ORDER BY $sortExpressions"
        } else ""

        var queryString = "SELECT * FROM products WHERE title LIKE '%' || ? || '%'"
        val args = mutableListOf<Any>(query)

        if (categories.isNotEmpty()) {
            val placeholders = categories.joinToString(separator = ", ") { "?" }
            queryString += " AND category IN ($placeholders)"
            args.addAll(categories)
        }

        queryString += " $sortClause"
        return SimpleSQLiteQuery(queryString, args.toTypedArray())
    }

}
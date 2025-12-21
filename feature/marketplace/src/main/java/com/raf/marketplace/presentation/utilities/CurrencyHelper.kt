package com.raf.marketplace.presentation.utilities

import android.os.Build
import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {

    private const val DOLLAR_PRICE = 16697

    fun Number.toLocalCurrency(): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return format.format(this)
    }

    fun Number.covertToIDR(pricePerDollar: Double = DOLLAR_PRICE.toDouble()): String {
        val price = this.toDouble() * pricePerDollar
        val localeID = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            Locale.of("id", "ID")
        } else {
            @Suppress("DEPRECATION")
            Locale("id", "ID")
        }
        val format = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }
        return format.format(price)
    }

}
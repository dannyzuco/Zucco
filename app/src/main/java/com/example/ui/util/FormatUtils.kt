package com.example.ui.util

import com.example.R
import java.util.Locale

/**
 * FormatUtils provides strict, production-ready formatting for ratings, experience years,
 * and strings across all card components.
 */
object FormatUtils {

    /**
     * Strict rating formatting ensuring all ratings are ALWAYS displayed with exactly one decimal place
     * (e.g., 4.4, 4.8, 5.0). Completely eliminates floating-point rounding errors (e.g., 4.399999999999995).
     */
    fun formatRating(rating: Double): String {
        return String.format(Locale.US, "%.1f", rating)
    }

    fun formatRating(rating: Float): String {
        return String.format(Locale.US, "%.1f", rating)
    }

    /**
     * Strictly enforces positive integers for years of experience: Math.max(1, Math.abs(years)).
     * Output is cleanly formatted as "X yrs exp" (e.g. "6 yrs exp").
     */
    fun formatExperience(years: Int): String {
        val safeYears = Math.max(1, Math.abs(years))
        return "$safeYears yrs exp"
    }

    /**
     * Long-form formatted experience (e.g., "6 Years Experience", "1 Year Experience").
     */
    fun formatExperienceLong(years: Int): String {
        val safeYears = Math.max(1, Math.abs(years))
        return "$safeYears ${if (safeYears == 1) "Year" else "Years"} Experience"
    }

    /**
     * Returns a high-quality category-specific trade placeholder image.
     * Guarantees accounts without custom uploaded photos never render empty dark grey boxes.
     */
    fun getCategoryFallbackDrawable(category: String): Int {
        val cat = category.trim().lowercase()
        return when {
            cat.contains("elec") || cat.contains("appliance") || cat.contains("tech") || cat.contains("gadget") ->
                R.drawable.work_electrical_1790095686624
            cat.contains("carp") || cat.contains("wood") || cat.contains("furn") || cat.contains("weld") ->
                R.drawable.work_carpenter_1790095709557
            cat.contains("cater") || cat.contains("food") || cat.contains("cook") ->
                R.drawable.ic_zucco_app_icon
            cat.contains("survey") || cat.contains("land") || cat.contains("map") || cat.contains("geo") ->
                R.drawable.ic_zucco_app_icon
            cat.contains("tail") || cat.contains("fash") || cat.contains("sew") || cat.contains("cloth") || cat.contains("hair") || cat.contains("beauty") ->
                R.drawable.ic_zucco_app_icon
            cat.contains("mech") || cat.contains("auto") || cat.contains("motor") ->
                R.drawable.ic_zucco_app_icon
            cat.contains("paint") || cat.contains("clean") || cat.contains("fumig") ->
                R.drawable.ic_zucco_app_icon
            else ->
                R.drawable.zucco_hero_1790095627427
        }
    }
}

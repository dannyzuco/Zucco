package com.example.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R

object TradeImageHelper {

    /**
     * Maps artisan categories to high-quality contextual trade fallback drawables.
     * Prevents empty dark grey/black gradient boxes when photos are missing or failing to load.
     */
    fun getFallbackDrawableForCategory(category: String?): Int {
        val cat = category?.trim()?.lowercase() ?: ""
        return when {
            cat.contains("electric") || cat.contains("gadget") || cat.contains("tech") || cat.contains("cctv") -> {
                R.drawable.work_electrical_1790095686624
            }
            cat.contains("plumb") || cat.contains("mechanic") || cat.contains("auto") || cat.contains("generator") || cat.contains("appliance") || cat.contains("ac") -> {
                R.drawable.ic_zucco_app_icon
            }
            cat.contains("paint") || cat.contains("weld") || cat.contains("brick") || cat.contains("mason") || cat.contains("iron") -> {
                R.drawable.ic_zucco_app_icon
            }
            cat.contains("carpent") || cat.contains("wood") || cat.contains("furniture") || cat.contains("interior") -> {
                R.drawable.work_carpenter_1790095709557
            }
            cat.contains("tailor") || cat.contains("fashion") || cat.contains("cloth") || cat.contains("hair") || cat.contains("beauty") || cat.contains("makeup") -> {
                R.drawable.ic_zucco_app_icon
            }
            cat.contains("clean") || cat.contains("fumigat") || cat.contains("pest") || cat.contains("event") || cat.contains("inspect") -> {
                R.drawable.ic_zucco_app_icon
            }
            else -> {
                R.drawable.zucco_hero_1790095627427
            }
        }
    }
}

/**
 * Reusable Composable that renders the artisan work/portfolio photo with guaranteed
 * high-resolution trade image fallbacks and proper aspect ratio (ContentScale.Crop).
 */
@Composable
fun TradeBannerImage(
    photoUrl: String?,
    category: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fallbackResId = TradeImageHelper.getFallbackDrawableForCategory(category)

    if (photoUrl.isNullOrBlank()) {
        Image(
            painter = painterResource(id = fallbackResId),
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photoUrl)
                .crossfade(true)
                .error(fallbackResId)
                .fallback(fallbackResId)
                .placeholder(fallbackResId)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

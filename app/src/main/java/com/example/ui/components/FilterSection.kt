package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationCatalog
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SoftGray
import com.example.ui.theme.ZuccoGold
import com.example.ui.theme.ZuccoGoldContainer
import com.example.ui.viewmodel.SortOption

@Composable
fun FilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCity: String,
    onCitySelect: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    onlyAvailable: Boolean,
    onToggleAvailable: (Boolean) -> Unit,
    resultCount: Int,
    savedCount: Int = 0,
    onViewSaved: (() -> Unit)? = null,
    recentSearches: List<String> = emptyList(),
    onClearRecentSearches: () -> Unit = {},
    onAddRecentSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 0: Filter by Category, 1: Filter by Location (Ota / Lagos Areas)
    var filterTabSelection by remember { mutableIntStateOf(0) }

    val popularLocations = when (selectedCity) {
        "Ota" -> listOf("Sango Ota", "Toll Gate Ota", "Iyana Iyesi Ota", "KM 10 / Canaanland", "Atan Ota", "Bells / Idiroko Rd")
        "Lagos" -> listOf("Ikeja", "Lekki Phase 1", "Yaba", "Victoria Island", "Surulere", "Alimosho / Egbeda", "Maryland", "Festac Town", "Ikorodu", "Oshodi")
        else -> listOf("Sango Ota", "Toll Gate Ota", "Iyana Iyesi Ota", "KM 10 / Canaanland", "Atan Ota", "Bells / Idiroko Rd", "Ikeja", "Lekki Phase 1", "Yaba", "Victoria Island", "Surulere", "Alimosho / Egbeda", "Maryland", "Festac Town", "Ikorodu", "Oshodi")
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // TOP SEARCH TEXT FIELD
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp,
            color = PureWhite
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = if (selectedCity == "All") "Search artisans, trades, or locations in Ota & Lagos..." else "Search $selectedCity artisans by name or trade...",
                        fontSize = 13.sp,
                        color = LightSlate
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SkyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = LightSlate,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DarkBlack,
                    unfocusedTextColor = DarkBlack,
                    cursorColor = NavyBlue,
                    focusedBorderColor = SkyBlue,
                    unfocusedBorderColor = SoftGray,
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = PureWhite,
                    focusedPlaceholderColor = LightSlate,
                    unfocusedPlaceholderColor = LightSlate,
                    focusedLeadingIconColor = NavyBlue,
                    unfocusedLeadingIconColor = LightSlate
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            onAddRecentSearch(searchQuery)
                        }
                    }
                ),
                textStyle = TextStyle(color = DarkBlack, fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // RECENT SEARCHES ROW
        if (recentSearches.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT SEARCHES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZuccoGold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Clear All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightSlate,
                        modifier = Modifier
                            .clickable { onClearRecentSearches() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    recentSearches.forEach { query ->
                        Surface(
                            onClick = { onSearchChange(query) },
                            color = MidnightCard,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MidnightCardBorder),
                            modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = LightSlate,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = query,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PRIMARY CITY & AREA FILTER CHIPS ROW (Single Scrollable Row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val cities = listOf(
                "All" to "All Areas",
                "Ota" to "Ota (Ogun)",
                "Lagos" to "Lagos State"
            )
            val otaLocations = listOf("Sango Ota", "Toll Gate Ota", "Iyana Iyesi Ota", "KM 10 / Canaanland", "Atan Ota", "Ota")
            val lagosLocations = listOf("Ikeja", "Lekki Phase 1", "Yaba", "Victoria Island", "Surulere", "Alimosho / Egbeda", "Maryland", "Festac Town", "Lagos")

            cities.forEach { (key, label) ->
                val isSelected = selectedCity == key
                val activeBgColor = when (key) {
                    "Ota" -> LeafGreen
                    "Lagos" -> SkyBlue
                    else -> ZuccoGold
                }
                val activeTextColor = if (key == "All") Color(0xFF0B1323) else PureWhite
                Surface(
                    onClick = {
                        if (key == "All") {
                            onCitySelect("All")
                            if (popularLocations.any { it.equals(searchQuery, ignoreCase = true) } ||
                                searchQuery.equals("Ota", ignoreCase = true) ||
                                searchQuery.equals("Lagos", ignoreCase = true)) {
                                onSearchChange("")
                            }
                        } else if (key == "Ota") {
                            onCitySelect("Ota")
                            if (lagosLocations.any { it.equals(searchQuery, ignoreCase = true) }) {
                                onSearchChange("")
                            }
                        } else if (key == "Lagos") {
                            onCitySelect("Lagos")
                            if (otaLocations.any { it.equals(searchQuery, ignoreCase = true) }) {
                                onSearchChange("")
                            }
                        }
                    },
                    color = if (isSelected) activeBgColor else MidnightCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isSelected) activeBgColor else MidnightCardBorder),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 40.dp)
                        .testTag("city_filter_$key")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        if (key == "Ota") {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isSelected) PureWhite else LeafGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        } else if (key == "Lagos") {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isSelected) PureWhite else SkyBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) activeTextColor else PureWhite
                        )
                    }
                }
            }

            if (savedCount > 0 && onViewSaved != null) {
                Surface(
                    onClick = { onViewSaved() },
                    color = MidnightCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ZuccoGold),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("filter_quick_saved_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = ZuccoGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saved ($savedCount)",
                            color = ZuccoGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // FILTER MODE SUB-TABS: Standalone "Categories" vs "Locations in Ota & Lagos"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { filterTabSelection = 0 },
                color = if (filterTabSelection == 0) NavyBlue else MidnightCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (filterTabSelection == 0) NavyBlue else MidnightCardBorder),
                modifier = Modifier.testTag("filter_tab_categories")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = if (filterTabSelection == 0) ZuccoGold else LightSlate,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Categories",
                        fontSize = 12.sp,
                        fontWeight = if (filterTabSelection == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (filterTabSelection == 0) ZuccoGold else PureWhite
                    )
                }
            }

            Surface(
                onClick = { filterTabSelection = 1 },
                color = if (filterTabSelection == 1) NavyBlue else MidnightCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (filterTabSelection == 1) NavyBlue else MidnightCardBorder),
                modifier = Modifier.testTag("filter_tab_locations")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (filterTabSelection == 1) ZuccoGold else LightSlate,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Popular Neighborhoods",
                        fontSize = 12.sp,
                        fontWeight = if (filterTabSelection == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (filterTabSelection == 1) ZuccoGold else PureWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // HORIZONTAL SCROLL CHIPS (Categories or Popular Locations)
        if (filterTabSelection == 0) {
            // CATEGORIES CHIPS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LocationCatalog.categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        onClick = {
                            if (category == "All Services") {
                                onCategorySelect("All Services")
                                if (LocationCatalog.categories.any { it.equals(searchQuery, ignoreCase = true) }) {
                                    onSearchChange("")
                                }
                            } else if (isSelected) {
                                onCategorySelect("All Services")
                            } else {
                                onCategorySelect(category)
                                if (LocationCatalog.categories.any { it.equals(searchQuery, ignoreCase = true) }) {
                                    onSearchChange("")
                                }
                            }
                        },
                        color = if (isSelected) ZuccoGold else MidnightCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (isSelected) ZuccoGold else MidnightCardBorder),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 38.dp)
                            .testTag("category_chip_${category.replace(" ", "_")}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) Color(0xFF0B1323) else PureWhite
                            )
                        }
                    }
                }
            }
        } else {
            // LOCATION PILLS (Ota & Lagos Areas)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                popularLocations.forEach { locName ->
                    val isFiltered = searchQuery.equals(locName, ignoreCase = true)
                    val isOtaLoc = locName.contains("Ota") || locName.contains("Canaanland")
                    val activeColor = if (isFiltered) ZuccoGold else (if (isOtaLoc) LeafGreen else SkyBlue)
                    Surface(
                        onClick = {
                            if (isFiltered) {
                                onSearchChange("")
                            } else {
                                onSearchChange(locName)
                                if (isOtaLoc && selectedCity == "Lagos") {
                                    onCitySelect("Ota")
                                } else if (!isOtaLoc && selectedCity == "Ota") {
                                    onCitySelect("Lagos")
                                }
                            }
                        },
                        color = if (isFiltered) ZuccoGold else MidnightCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, activeColor),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 38.dp)
                            .testTag("location_pill_${locName.replace(" ", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isFiltered) Color(0xFF0B1323) else activeColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locName,
                                fontSize = 11.sp,
                                fontWeight = if (isFiltered) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isFiltered) Color(0xFF0B1323) else PureWhite
                            )
                        }
                    }
                }
            }
        }

        // RESULTS BAR & HIGH CONTRAST SORTING BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$resultCount Artisan${if (resultCount != 1) "s" else ""} Found Near You",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = ZuccoGold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // High-Contrast Sort Button
                Surface(
                    color = if (sortOption == SortOption.CLOSEST) ZuccoGoldContainer else MidnightCard,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (sortOption == SortOption.CLOSEST) ZuccoGold else Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onSortChange(
                                if (sortOption == SortOption.CLOSEST) SortOption.TOP_RATED else SortOption.CLOSEST
                            )
                        }
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("sort_toggle_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = ZuccoGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = sortOption.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (sortOption == SortOption.CLOSEST) ZuccoGold else PureWhite
                        )
                    }
                }

                // High-Contrast Availability Button
                Surface(
                    color = if (onlyAvailable) LeafGreenContainer else MidnightCard,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (onlyAvailable) LeafGreen else Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onToggleAvailable(!onlyAvailable) }
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("only_available_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (onlyAvailable) LeafGreen else LightSlate, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Available",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (onlyAvailable) LeafGreen else OffWhite
                        )
                    }
                }
            }
        }
    }
}

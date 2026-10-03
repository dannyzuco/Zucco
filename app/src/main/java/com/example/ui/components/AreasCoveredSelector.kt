package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightInputBg
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ZuccoGold

data class LgaAreaGroup(
    val lgaName: String,
    val city: String,
    val subLocations: List<String>
)

object AreasCatalog {
    val lgaGroups = listOf(
        LgaAreaGroup(
            lgaName = "Ado-Odo / Ota LGA",
            city = "Ota (Ogun)",
            subLocations = listOf("Sango Ota", "Iyana Iyesi", "Atan Ota", "KM 10 / Canaanland", "Toll Gate Ota", "Ijoko Ota", "Idiroko Road")
        ),
        LgaAreaGroup(
            lgaName = "Ikeja LGA",
            city = "Lagos",
            subLocations = listOf("Computer Village", "Allen Avenue", "Opebi", "Maryland", "GRA Ikeja")
        ),
        LgaAreaGroup(
            lgaName = "Eti-Osa LGA",
            city = "Lagos",
            subLocations = listOf("Lekki Phase 1", "Victoria Island", "Ikoyi", "Ajah", "Sangotedo")
        ),
        LgaAreaGroup(
            lgaName = "Alimosho LGA",
            city = "Lagos",
            subLocations = listOf("Egbeda", "Iyana Ipaja", "Igando", "Ikotun")
        ),
        LgaAreaGroup(
            lgaName = "Surulere & Mainland LGAs",
            city = "Lagos",
            subLocations = listOf("Yaba", "Surulere", "Ebute Metta", "Ojuelegba")
        ),
        LgaAreaGroup(
            lgaName = "Kosofe & Oshodi LGAs",
            city = "Lagos",
            subLocations = listOf("Oshodi", "Isolo", "Ketu", "Ogudu", "Magodo")
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AreasCoveredSelector(
    selectedAreas: List<String>,
    onAreaToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = ZuccoGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Areas Covered (LGAs & Sub-Locations) *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Tap to select all local government areas & sub-locations your workshop serves",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightSlate
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AreasCatalog.lgaGroups.forEach { group ->
            Surface(
                color = MidnightInputBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${group.lgaName} (${group.city})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZuccoGold
                        )

                        val selectedInGroup = group.subLocations.count { it in selectedAreas }
                        if (selectedInGroup > 0) {
                            Surface(
                                color = LeafGreenContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "$selectedInGroup Selected",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LeafGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        group.subLocations.forEach { areaName ->
                            val isSelected = selectedAreas.contains(areaName)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onAreaToggle(areaName) },
                                label = {
                                    Text(
                                        text = areaName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PureWhite,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LeafGreen,
                                    selectedLabelColor = PureWhite,
                                    containerColor = MidnightCard,
                                    labelColor = LightSlate
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MidnightCardBorder,
                                    selectedBorderColor = LeafGreen
                                ),
                                modifier = Modifier.testTag("area_chip_${areaName.replace(" ", "_")}")
                            )
                        }
                    }
                }
            }
        }
    }
}

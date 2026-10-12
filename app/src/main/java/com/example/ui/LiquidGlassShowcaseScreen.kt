package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidChip
import com.example.components.LiquidClearGlassCircleButton
import com.example.components.LiquidFeatureCard
import com.example.components.LiquidGlassCard
import com.example.components.LiquidInputField
import com.example.components.LiquidMainGlassPanel
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidRoundIconButton
import com.example.components.LiquidSearchBarWithCheckbox
import com.example.components.LiquidSecondaryButton
import com.example.components.LiquidSuccessChip
import com.example.components.LiquidTabs
import com.example.components.LiquidToggleSwitch
import com.example.ui.theme.LiquidGlassTokens
import com.example.ui.theme.LiquidTextDark
import com.example.ui.theme.LiquidTextMuted

@Composable
fun LiquidGlassShowcaseScreen(
    onNavigateToFloodApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchProjectsQuery by remember { mutableStateOf("") }
    var searchBarQuery by remember { mutableStateOf("") }
    var inputFieldQuery by remember { mutableStateOf("") }
    var isSearchChecked by remember { mutableStateOf(true) }
    var isToggleActive by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var statusFeedback by remember { mutableStateOf("Tap any component to interact") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Main Glass Panel (Frosted glass container matching exact specification)
        LiquidMainGlassPanel(
            title = "Liquid Glass UI Kit",
            onMinusClick = { statusFeedback = "Panel minimized / toggled" }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LiquidGlassTokens.ItemGap)
            ) {
                // Info banner
                Text(
                    text = "High-fidelity recreation of the Liquid Glassmorphism design system. All 12 components crafted with soft colored shadows, glossy top shines, and iridescent borders.",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = LiquidTextMuted,
                        lineHeight = 18.sp
                    )
                )

                // 1 & 2: Primary and Secondary Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LiquidPrimaryButton(
                        text = "Primary Button",
                        modifier = Modifier.weight(1f),
                        onClick = { statusFeedback = "Primary Purple Button clicked!" }
                    )
                    LiquidSecondaryButton(
                        text = "Secondary Button",
                        modifier = Modifier.weight(1f),
                        onClick = { statusFeedback = "Secondary Teal Button clicked!" }
                    )
                }

                // 3 & 4: Round Icon Button & Clear Glass Circle Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3. Round Icon Button (solid soft-white circle with "+")
                        LiquidRoundIconButton(
                            icon = Icons.Default.Add,
                            onClick = { statusFeedback = "Solid Round Icon Button (+) tapped" }
                        )
                        Text(
                            text = "Round Icon Button",
                            style = TextStyle(fontSize = 13.sp, color = LiquidTextDark, fontWeight = FontWeight.Medium)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 4. Clear Glass Circle Button (fully see-through bubble)
                        LiquidClearGlassCircleButton(
                            icon = Icons.Outlined.WaterDrop,
                            onClick = { statusFeedback = "Clear Glass Bubble Button tapped" }
                        )
                        Text(
                            text = "Glass Circle",
                            style = TextStyle(fontSize = 13.sp, color = LiquidTextDark, fontWeight = FontWeight.Medium)
                        )
                    }
                }

                // 5: Glass Card (top-right blue-teal-lavender transparent gradient with iridescent edge glow)
                LiquidGlassCard(
                    appName = "Nitestudio Cloud",
                    searchQuery = searchProjectsQuery,
                    onQueryChange = { searchProjectsQuery = it },
                    onAddClick = { statusFeedback = "Added new item inside Glass Card" }
                )

                // 6: Search Bar with Checkbox
                LiquidSearchBarWithCheckbox(
                    query = searchBarQuery,
                    onQueryChange = { searchBarQuery = it },
                    isChecked = isSearchChecked,
                    onToggleCheck = {
                        isSearchChecked = !isSearchChecked
                        statusFeedback = "Search checkbox toggled: $isSearchChecked"
                    },
                    onActionClick = { statusFeedback = "Executed search for: '$searchBarQuery'" }
                )

                // 7: Input Field (soft-white pill, grey placeholder, search icon)
                LiquidInputField(
                    value = inputFieldQuery,
                    onValueChange = { inputFieldQuery = it },
                    placeholder = "Create new workspace",
                    onTrailingIconClick = { statusFeedback = "Created workspace '$inputFieldQuery'" }
                )

                // 8, 9, 10: Chip, Toggle Switch, Success Chip in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 8. Chip (example: "Nitestudio")
                    LiquidChip(
                        text = "Nitestudio",
                        onClick = { statusFeedback = "Selected 'Nitestudio' chip" }
                    )

                    // 9. Toggle Switch (glass pill track with soft-white knob holding "+")
                    LiquidToggleSwitch(
                        checked = isToggleActive,
                        onCheckedChange = {
                            isToggleActive = it
                            statusFeedback = "Liquid Toggle Switch is now: ${if (it) "ON" else "OFF"}"
                        }
                    )

                    // 10. Success Chip (example: "whitespace" with green tick)
                    LiquidSuccessChip(
                        text = "whitespace",
                        onClick = { statusFeedback = "Selected 'whitespace' success chip" }
                    )
                }

                // 11: Tabs / Segmented Control ("Tabs" active, "Toast" inactive with purple underline)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidTabs(
                        tabs = listOf("Tabs", "Toast"),
                        selectedIndex = selectedTabIndex,
                        onTabSelected = {
                            selectedTabIndex = it
                            statusFeedback = "Selected Tab: ${if (it == 0) "Tabs" else "Toast"}"
                        }
                    )
                    Text(
                        text = "Segmented Control",
                        style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                    )
                }

                // 12: Feature Card (bottom-right: big gradient card pink -> lavender -> blue -> cyan)
                LiquidFeatureCard(
                    title = "Modern Glass UI Design",
                    subtitle = "Explore more features",
                    buttonText = "Upgrade plan",
                    indicatorText = "PRO",
                    onButtonClick = {
                        statusFeedback = "Opening Flood Intelligence Suite..."
                        onNavigateToFloodApp()
                    }
                )

                // Status feedback toast bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "• $statusFeedback",
                        style = TextStyle(fontSize = 13.sp, color = LiquidTextMuted, fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

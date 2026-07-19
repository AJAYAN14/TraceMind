package com.jian.tracemind.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.jian.tracemind.core.ui.extensions.traceShadow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.LiquidToggle
import com.jian.tracemind.core.ui.components.SectionLabel
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

enum class ProfileThemeMode {
    SYSTEM, LIGHT, DARK
}

@Composable
fun ProfileScreen(
    innerPadding: PaddingValues, 
    modifier: Modifier = Modifier,
    themeMode: ProfileThemeMode = ProfileThemeMode.SYSTEM,
    onThemeModeChange: (ProfileThemeMode) -> Unit = {}
) {
    val localBackdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background layer providing the backdrop
        // Background color doesn't change based on isDarkMode as per user request
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                .layerBackdrop(localBackdrop)
        )

        // 2. Foreground layer containing scrolling content
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.onBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "TraceMind User",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: 10086",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            SectionLabel("偏好设置")
            Spacer(modifier = Modifier.height(16.dp))

            // Settings Card
            Surface(
                modifier = Modifier.fillMaxWidth().traceShadow(borderRadius = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (themeMode == ProfileThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme Icon",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "深色模式",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }

                    LiquidToggle(
                        selected = { themeMode == ProfileThemeMode.DARK },
                        onSelect = { isDark -> 
                            onThemeModeChange(if (isDark) ProfileThemeMode.DARK else ProfileThemeMode.LIGHT) 
                        },
                        backdrop = localBackdrop
                    )
                }
            }

        }
    }
}

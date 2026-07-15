package com.jian.tracemind.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A generic liquid-glass-styled AppBar layout.
 *
 * Provides three slots: a navigation icon on the left, a title in the center,
 * and action buttons on the right. The actual liquid glass effects are applied
 * on the individual buttons (LiquidIconButton / LiquidTextButton), not on the
 * AppBar container itself.
 *
 * @param modifier Modifier for the AppBar row
 * @param navigationIcon Optional left-side navigation icon slot (e.g. back button)
 * @param title Optional center title slot
 * @param actions Optional right-side actions slot (one or more buttons)
 */
@Composable
fun LiquidAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    centerTitle: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left section: navigation icon + title (if not centered)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (navigationIcon != null) {
                    navigationIcon()
                    if (title != null && !centerTitle) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
                if (title != null && !centerTitle) {
                    Box(modifier = Modifier.weight(1f, fill = false)) {
                        title()
                    }
                }
            }

            // Right section: action buttons
            if (actions != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
        
        if (title != null && centerTitle) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                title()
            }
        }
    }
}

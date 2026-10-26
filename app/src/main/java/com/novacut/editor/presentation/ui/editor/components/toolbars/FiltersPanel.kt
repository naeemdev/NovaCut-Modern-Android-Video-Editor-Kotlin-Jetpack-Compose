package com.novacut.editor.presentation.ui.editor.components.toolbars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.domain.model.FilterType
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSecondary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun FiltersPanel(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface)
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Color Filters",
                color = StudioTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FilterType.values().forEach { filter ->
                val isSelected = filter == selectedFilter

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onFilterSelected(filter) }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(getFilterBrush(filter))
                            .border(
                                width = if (isSelected) 2.5f.dp else 1.dp,
                                color = if (isSelected) StudioPrimary else StudioBorder,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = filter.displayName,
                        color = if (isSelected) StudioPrimary else StudioTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

private fun getFilterBrush(filter: FilterType): Brush {
    return when (filter) {
        FilterType.NONE -> Brush.linearGradient(listOf(Color(0xFF4A4E69), Color(0xFF22223B)))
        FilterType.CINEMATIC -> Brush.linearGradient(listOf(Color(0xFF0077B6), Color(0xFFF77F00)))
        FilterType.CYBERPUNK -> Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFF00F0FF)))
        FilterType.NOIR -> Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFF101010)))
        FilterType.VINTAGE -> Brush.linearGradient(listOf(Color(0xFFD4A373), Color(0xFFCCD5AE)))
        FilterType.GOLDEN_HOUR -> Brush.linearGradient(listOf(Color(0xFFFFB703), Color(0xFFFB8500)))
        FilterType.MOODY_TEAL -> Brush.linearGradient(listOf(Color(0xFF0D3B66), Color(0xFF2EC4B6)))
        FilterType.VIBRANT -> Brush.linearGradient(listOf(Color(0xFFFF5964), Color(0xFF35A7FF)))
    }
}

@Preview(name = "Filters Panel Preview", showBackground = true)
@Composable
fun FiltersPanelPreview() {
    NovaCutTheme {
        FiltersPanel(
            selectedFilter = FilterType.CINEMATIC,
            onFilterSelected = {},
            onClose = {}
        )
    }
}

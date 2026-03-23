package com.example.autoservice_desktop.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoservice_desktop.core.ui.theme.AppColors

@Composable
internal fun NavigationItem(
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) {
        AppColors.SelectedItemBackground
    } else {
        AppColors.SidebarBackground
    }

    val textColor = if (selected) {
        AppColors.SelectedItemText
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
package com.example.advena.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(24.dp),
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor: Color? = null,
    disabledContentColor: Color? = null,
    border: BorderStroke? = null,
    useAlphaForDisabled: Boolean = true,
    enabledAlpha: Float = 0.75f,
    disabledAlpha: Float = 0.4f,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    val finalContainerColor = if (useAlphaForDisabled && enabled) {
        containerColor.copy(alpha = enabledAlpha)
    } else if (useAlphaForDisabled && !enabled) {
        containerColor.copy(alpha = disabledAlpha)
    } else {
        containerColor
    }

    val finalDisabledContainerColor = disabledContainerColor ?:
    if (useAlphaForDisabled) {
        containerColor.copy(alpha = disabledAlpha)
    } else {
        containerColor
    }

    val finalDisabledContentColor = disabledContentColor ?:
    contentColor.copy(alpha = 0.5f)

    val borderColor = border ?: BorderStroke(
        2.dp,
        if (!enabled) disabledContainerColor ?: containerColor else containerColor
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        border = borderColor,
        colors = ButtonDefaults.buttonColors(
            containerColor = finalContainerColor,
            contentColor = contentColor,
            disabledContainerColor = finalDisabledContainerColor,
            disabledContentColor = finalDisabledContentColor
        ),
        contentPadding = contentPadding
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}
@Composable
fun AppIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    buttonModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = buttonModifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = iconModifier
        )
    }
}
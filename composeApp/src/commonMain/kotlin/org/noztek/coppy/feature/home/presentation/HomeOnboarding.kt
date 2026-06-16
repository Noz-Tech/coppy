package org.noztek.coppy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val TooltipWidthPxFallback = 280

internal enum class HomeTutorialTarget {
    AddEntry,
    Folders,
    FirstEntry,
    Settings,
}

internal data class HomeTutorialStep(
    val title: String,
    val body: String,
    val target: HomeTutorialTarget? = null,
)

@Composable
internal fun HomeTutorialOverlay(
    step: HomeTutorialStep,
    stepIndex: Int,
    stepCount: Int,
    rootSize: IntSize,
    targetBounds: Rect?,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onDone: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isLastStep = stepIndex == stepCount - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}
            )
    ) {
        targetBounds?.let { bounds ->
            TutorialSpotlight(bounds = bounds)
        }

        TutorialTooltip(
            step = step,
            stepIndex = stepIndex,
            stepCount = stepCount,
            rootSize = rootSize,
            targetBounds = targetBounds,
            isLastStep = isLastStep,
            onNext = onNext,
            onSkip = onSkip,
            onDone = onDone,
        )
    }
}

@Composable
private fun TutorialSpotlight(bounds: Rect) {
    val density = LocalDensity.current
    val padding = 8.dp
    val paddingPx = with(density) { padding.roundToPx() }
    val widthDp = with(density) { bounds.width.toDp() + padding * 2 }
    val heightDp = with(density) { bounds.height.toDp() + padding * 2 }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = bounds.left.roundToInt() - paddingPx,
                    y = bounds.top.roundToInt() - paddingPx
                )
            }
            .width(widthDp)
            .height(heightDp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(18.dp)
            )
    )
}

@Composable
private fun TutorialTooltip(
    step: HomeTutorialStep,
    stepIndex: Int,
    stepCount: Int,
    rootSize: IntSize,
    targetBounds: Rect?,
    isLastStep: Boolean,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onDone: () -> Unit,
) {
    val density = LocalDensity.current
    val horizontalMarginPx = with(density) { 16.dp.roundToPx() }
    val verticalMarginPx = with(density) { 20.dp.roundToPx() }
    val tooltipWidthPx = with(density) { 300.dp.roundToPx() }.takeIf { it > 0 } ?: TooltipWidthPxFallback

    val xPx: Int
    val yPx: Int

    if (targetBounds == null || rootSize == IntSize.Zero) {
        val centeredX = if (rootSize == IntSize.Zero) {
            horizontalMarginPx
        } else {
            (rootSize.width - tooltipWidthPx) / 2
        }
        val maxX = (rootSize.width - tooltipWidthPx - horizontalMarginPx).coerceAtLeast(horizontalMarginPx)
        xPx = centeredX.coerceIn(horizontalMarginPx, maxX)
        yPx = if (rootSize == IntSize.Zero) {
            verticalMarginPx + 36
        } else {
            ((rootSize.height - with(density) { 220.dp.roundToPx() }) / 2).coerceAtLeast(verticalMarginPx)
        }
    } else {
        val centeredX = targetBounds.center.x.roundToInt() - (tooltipWidthPx / 2)
        val maxX = (rootSize.width - tooltipWidthPx - horizontalMarginPx).coerceAtLeast(horizontalMarginPx)
        xPx = centeredX.coerceIn(horizontalMarginPx, maxX)

        val targetBottom = targetBounds.bottom.roundToInt()
        val targetTop = targetBounds.top.roundToInt()
        val belowY = targetBottom + verticalMarginPx
        val aboveY = targetTop - with(density) { 220.dp.roundToPx() }

        yPx = if (belowY + with(density) { 200.dp.roundToPx() } <= rootSize.height) {
            belowY
        } else {
            aboveY.coerceAtLeast(verticalMarginPx)
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 10.dp,
        modifier = Modifier
            .offset { IntOffset(xPx, yPx) }
            .width(300.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Step ${stepIndex + 1} of $stepCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = step.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onSkip)
                )

                Text(
                    text = if (isLastStep) "Done" else "Next",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = if (isLastStep) onDone else onNext)
                )
            }
        }
    }
}

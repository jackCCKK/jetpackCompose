package com.example.jetpackcomposeviewtrackingdemo.viewtracking

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.customModifier(
    onFullyVisible: (() -> Unit)? = null,
    onNotFullyVisible: (() -> Unit)? = null,
    fullyVisibleThreshold: Dp = 0.dp
): Modifier = composed {
    var hasBecomeFullyVisible by remember { mutableStateOf(false) }
    val fullyVisibleThresholdPxValue = LocalDensity.current.run { fullyVisibleThreshold.toPx() }

    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp.toPx
    val screenHeightPx = configuration.screenHeightDp.toPx

    onGloballyPositioned { layoutCoordinates ->
        val (width, height) = layoutCoordinates.size
        val (x1, y1) = layoutCoordinates.positionInRoot()
        val x2 = x1 + width
        val y2 = y1 + height

        val isFullyVisible = (x1 >= 0 && y1 >= 0) && (x2 <= screenWidthPx && y2 <= screenHeightPx)

        if (isFullyVisible && !hasBecomeFullyVisible) {
            hasBecomeFullyVisible = true
            onFullyVisible?.invoke()
        } else if (!isFullyVisible && hasBecomeFullyVisible) {
            hasBecomeFullyVisible = false
            onNotFullyVisible?.invoke()
        }
    }
}

typealias VisibilityChangeAction = () -> Unit

@Composable
fun Modifier.customModifier2(
    visibilityThreshold: Float,
    onFullyVisible: VisibilityChangeAction? = null,
    onNotFullyVisible: VisibilityChangeAction? = null,
): Modifier = composed {
    var hasBecomeFullyVisible by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.toPx }
    val screenHeightPx = with(density) { configuration.screenHeightDp.toPx }

    onGloballyPositioned { layoutCoordinates ->
        val (width, height) = layoutCoordinates.size
        val (x1, y1) = layoutCoordinates.positionInRoot()
        val x2 = x1 + width
        val y2 = y1 + height

        val viewArea = width * height

        val visibleX1 = maxOf(x1, 0f)
        val visibleY1 = maxOf(y1, 0f)
        val visibleX2 = minOf(x2, screenWidthPx)
        val visibleY2 = minOf(y2, screenHeightPx)

        val visibleWidth = maxOf(0f, visibleX2 - visibleX1)
        val visibleHeight = maxOf(0f, visibleY2 - visibleY1)

        val visibleArea = visibleWidth * visibleHeight

        val isVisible = visibleArea / viewArea >= visibilityThreshold

        if (isVisible && !hasBecomeFullyVisible) {
            hasBecomeFullyVisible = true
            onFullyVisible?.invoke()
        } else if (!isVisible && hasBecomeFullyVisible) {
            hasBecomeFullyVisible = false
            onNotFullyVisible?.invoke()
        }
    }
}


val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
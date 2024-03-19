@file:Suppress("TransitionPropertiesLabel", "KotlinRedundantDiagnosticSuppress")

package androidx.compose.material3

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.math.max
import kotlin.math.min


@Composable
internal fun DropdownMenuContent(
    expandedState: MutableTransitionState<Boolean>,
    transformOriginState: MutableState<TransformOrigin>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // Menu open/close animation.
    @Suppress("DEPRECATION")
    val transition = updateTransition(expandedState, "DropDownMenu")

    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(
                    durationMillis = InTransitionDuration,
                    easing = LinearOutSlowInEasing
                )
            } else {
                // Expanded to dismissed.
                tween(
                    durationMillis = 1,
                    delayMillis = OutTransitionDuration - 1
                )
            }
        }
    ) { expanded ->
        if (expanded) 1f else 0.8f
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = 30)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = OutTransitionDuration)
            }
        }
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    Surface(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
            transformOrigin = transformOriginState.value
        },
        shape = MenuTokens.ContainerShape.value,
        color = MaterialTheme.colorScheme.fromToken(MenuTokens.ContainerColor),
        tonalElevation = MenuTokens.ContainerElevation,
        shadowElevation = MenuTokens.ContainerElevation
    ) {
        Column(
            modifier = modifier
                .padding(vertical = DropdownMenuVerticalPadding)
                .width(IntrinsicSize.Max)
                .verticalScroll(scrollState),
            content = content
        )
    }
}


internal fun calculateTransformOrigin(
    anchorBounds: IntRect,
    menuBounds: IntRect
): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= anchorBounds.right -> 0f
        menuBounds.right <= anchorBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter =
                (max(anchorBounds.left, menuBounds.left) +
                        min(anchorBounds.right, menuBounds.right)) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= anchorBounds.bottom -> 0f
        menuBounds.bottom <= anchorBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter =
                (max(anchorBounds.top, menuBounds.top) +
                        min(anchorBounds.bottom, menuBounds.bottom)) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}

internal val MenuVerticalMargin = 48.dp
internal val DropdownMenuVerticalPadding = 8.dp

internal const val InTransitionDuration = 120
internal const val OutTransitionDuration = 75


internal object MenuTokens {
    val ContainerColor = ColorSchemeKeyTokens.Surface
    val ContainerElevation = ElevationTokens.Level2
    val ContainerShape = ShapeKeyTokens.CornerExtraSmall
}


internal enum class ColorSchemeKeyTokens {
    Background,
    Error,
    ErrorContainer,
    InverseOnSurface,
    InversePrimary,
    InverseSurface,
    OnBackground,
    OnError,
    OnErrorContainer,
    OnPrimary,
    OnPrimaryContainer,
    OnSecondary,
    OnSecondaryContainer,
    OnSurface,
    OnSurfaceVariant,
    OnTertiary,
    OnTertiaryContainer,
    Outline,
    OutlineVariant,
    Primary,
    PrimaryContainer,
    Scrim,
    Secondary,
    SecondaryContainer,
    Surface,
    SurfaceBright,
    SurfaceContainer,
    SurfaceContainerHigh,
    SurfaceContainerHighest,
    SurfaceContainerLow,
    SurfaceContainerLowest,
    SurfaceDim,
    SurfaceTint,
    SurfaceVariant,
    Tertiary,
    TertiaryContainer,
}


internal object ElevationTokens {
    val Level2 = 3.0.dp
}


internal enum class ShapeKeyTokens {
    CornerExtraLarge,
    CornerExtraLargeTop,
    CornerExtraSmall,
    CornerExtraSmallTop,
    CornerFull,
    CornerLarge,
    CornerLargeEnd,
    CornerLargeTop,
    CornerMedium,
    CornerNone,
    CornerSmall,
}


internal object MenuPosition {
    /**
     * An interface to calculate the vertical position of a menu with respect to its anchor and
     * window. The returned y-coordinate is relative to the window.
     *
     * @see PopupPositionProvider
     */
    @Stable
    fun interface Vertical {
        fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuHeight: Int,
        ): Int
    }

    /**
     * An interface to calculate the horizontal position of a menu with respect to its anchor,
     * window, and layout direction. The returned x-coordinate is relative to the window.
     *
     * @see PopupPositionProvider
     */
    @Stable
    fun interface Horizontal {
        fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection,
        ): Int
    }

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the start of the menu to the start of the
     * anchor.
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    fun startToAnchorStart(offset: Int = 0): Horizontal =
        AnchorAlignmentOffsetPosition.Horizontal(
            menuAlignment = Alignment.Start,
            anchorAlignment = Alignment.Start,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the end of the menu to the end of the
     * anchor.
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    fun endToAnchorEnd(offset: Int = 0): Horizontal =
        AnchorAlignmentOffsetPosition.Horizontal(
            menuAlignment = Alignment.End,
            anchorAlignment = Alignment.End,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the left of the menu to the left of the
     * window.
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window.
     */
    fun leftToWindowLeft(margin: Int = 0): Horizontal =
        WindowAlignmentMarginPosition.Horizontal(
            alignment = AbsoluteAlignment.Left,
            margin = margin,
        )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the right of the menu to the right of the
     * window.
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window.
     */
    fun rightToWindowRight(margin: Int = 0): Horizontal =
        WindowAlignmentMarginPosition.Horizontal(
            alignment = AbsoluteAlignment.Right,
            margin = margin,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the bottom of the
     * anchor.
     */
    fun topToAnchorBottom(offset: Int = 0): Vertical =
        AnchorAlignmentOffsetPosition.Vertical(
            menuAlignment = Alignment.Top,
            anchorAlignment = Alignment.Bottom,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the top of the
     * anchor.
     */
    fun bottomToAnchorTop(offset: Int = 0): Vertical =
        AnchorAlignmentOffsetPosition.Vertical(
            menuAlignment = Alignment.Bottom,
            anchorAlignment = Alignment.Top,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the center of the menu to the top of the
     * anchor.
     */
    fun centerToAnchorTop(offset: Int = 0): Vertical =
        AnchorAlignmentOffsetPosition.Vertical(
            menuAlignment = Alignment.CenterVertically,
            anchorAlignment = Alignment.Top,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the top of the
     * window.
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window.
     */
    fun topToWindowTop(margin: Int = 0): Vertical =
        WindowAlignmentMarginPosition.Vertical(
            alignment = Alignment.Top,
            margin = margin,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the bottom of the
     * window.
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window.
     */
    fun bottomToWindowBottom(margin: Int = 0): Vertical =
        WindowAlignmentMarginPosition.Vertical(
            alignment = Alignment.Bottom,
            margin = margin,
        )
}


@Immutable
internal object WindowAlignmentMarginPosition {
    /**
     * A [MenuPosition.Horizontal] which horizontally aligns the menu within the window according
     * to the given [alignment].
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window. If this is not possible, i.e.,
     * the menu is too wide, then it is centered horizontally instead.
     */
    @Immutable
    data class Horizontal(
        private val alignment: Alignment.Horizontal,
        private val margin: Int,
    ) : MenuPosition.Horizontal {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection,
        ): Int {
            if (menuWidth >= windowSize.width - 2 * margin) {
                return Alignment.CenterHorizontally.align(
                    size = menuWidth,
                    space = windowSize.width,
                    layoutDirection = layoutDirection,
                )
            }
            val x = alignment.align(
                size = menuWidth,
                space = windowSize.width,
                layoutDirection = layoutDirection,
            )
            return x.coerceIn(margin, windowSize.width - margin - menuWidth)
        }
    }

    /**
     * A [MenuPosition.Vertical] which vertically aligns the menu within the window according to
     * the given [alignment].
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window. If this is not possible, i.e.,
     * the menu is too tall, then it is centered vertically instead.
     */
    @Immutable
    data class Vertical(
        private val alignment: Alignment.Vertical,
        private val margin: Int,
    ) : MenuPosition.Vertical {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuHeight: Int,
        ): Int {
            if (menuHeight >= windowSize.height - 2 * margin) {
                return Alignment.CenterVertically.align(
                    size = menuHeight,
                    space = windowSize.height,
                )
            }
            val y = alignment.align(
                size = menuHeight,
                space = windowSize.height,
            )
            return y.coerceIn(margin, windowSize.height - margin - menuHeight)
        }
    }
}

@Immutable
internal object AnchorAlignmentOffsetPosition {
    /**
     * A [MenuPosition.Horizontal] which horizontally aligns the given [menuAlignment] with the
     * given [anchorAlignment].
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    @Immutable
    data class Horizontal(
        private val menuAlignment: Alignment.Horizontal,
        private val anchorAlignment: Alignment.Horizontal,
        private val offset: Int,
    ) : MenuPosition.Horizontal {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection,
        ): Int {
            val anchorAlignmentOffset = anchorAlignment.align(
                size = 0,
                space = anchorBounds.width,
                layoutDirection = layoutDirection,
            )
            val menuAlignmentOffset = -menuAlignment.align(
                size = 0,
                space = menuWidth,
                layoutDirection,
            )
            val resolvedOffset = if (layoutDirection == LayoutDirection.Ltr) offset else -offset
            return anchorBounds.left + anchorAlignmentOffset + menuAlignmentOffset + resolvedOffset
        }
    }

    /**
     * A [MenuPosition.Vertical] which vertically aligns the given [menuAlignment] with the given
     * [anchorAlignment].
     */
    @Immutable
    data class Vertical(
        private val menuAlignment: Alignment.Vertical,
        private val anchorAlignment: Alignment.Vertical,
        private val offset: Int,
    ) : MenuPosition.Vertical {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuHeight: Int,
        ): Int {
            val anchorAlignmentOffset = anchorAlignment.align(
                size = 0,
                space = anchorBounds.height,
            )
            val menuAlignmentOffset = -menuAlignment.align(
                size = 0,
                space = menuHeight,
            )
            return anchorBounds.top + anchorAlignmentOffset + menuAlignmentOffset + offset
        }
    }
}

internal val ShapeKeyTokens.value: Shape
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.shapes.fromToken(this)

internal fun Shapes.fromToken(value: ShapeKeyTokens): Shape {
    return when (value) {
        ShapeKeyTokens.CornerExtraLarge -> extraLarge
        ShapeKeyTokens.CornerExtraLargeTop -> extraLarge.top()
        ShapeKeyTokens.CornerExtraSmall -> extraSmall
        ShapeKeyTokens.CornerExtraSmallTop -> extraSmall.top()
        ShapeKeyTokens.CornerFull -> CircleShape
        ShapeKeyTokens.CornerLarge -> large
        ShapeKeyTokens.CornerLargeEnd -> large.end()
        ShapeKeyTokens.CornerLargeTop -> large.top()
        ShapeKeyTokens.CornerMedium -> medium
        ShapeKeyTokens.CornerNone -> RectangleShape
        ShapeKeyTokens.CornerSmall -> small
    }
}

/** Helper function for component shape tokens. Used to grab the top values of a shape parameter. */
internal fun CornerBasedShape.top(): CornerBasedShape {
    return copy(bottomStart = CornerSize(0.0.dp), bottomEnd = CornerSize(0.0.dp))
}

/** Helper function for component shape tokens. Used to grab the end values of a shape parameter. */
internal fun CornerBasedShape.end(): CornerBasedShape {
    return copy(topStart = CornerSize(0.0.dp), bottomStart = CornerSize(0.0.dp))
}

/**
 * Helper function for component color tokens. Here is an example on how to use component color
 * tokens:
 * ``MaterialTheme.colorScheme.fromToken(ExtendedFabBranded.BrandedContainerColor)``
 */
@Stable
internal fun ColorScheme.fromToken(value: ColorSchemeKeyTokens): Color {
    return when (value) {
        ColorSchemeKeyTokens.Background -> background
        ColorSchemeKeyTokens.Error -> error
        ColorSchemeKeyTokens.ErrorContainer -> errorContainer
        ColorSchemeKeyTokens.InverseOnSurface -> inverseOnSurface
        ColorSchemeKeyTokens.InversePrimary -> inversePrimary
        ColorSchemeKeyTokens.InverseSurface -> inverseSurface
        ColorSchemeKeyTokens.OnBackground -> onBackground
        ColorSchemeKeyTokens.OnError -> onError
        ColorSchemeKeyTokens.OnErrorContainer -> onErrorContainer
        ColorSchemeKeyTokens.OnPrimary -> onPrimary
        ColorSchemeKeyTokens.OnPrimaryContainer -> onPrimaryContainer
        ColorSchemeKeyTokens.OnSecondary -> onSecondary
        ColorSchemeKeyTokens.OnSecondaryContainer -> onSecondaryContainer
        ColorSchemeKeyTokens.OnSurface -> onSurface
        ColorSchemeKeyTokens.OnSurfaceVariant -> onSurfaceVariant
        ColorSchemeKeyTokens.SurfaceTint -> surfaceTint
        ColorSchemeKeyTokens.OnTertiary -> onTertiary
        ColorSchemeKeyTokens.OnTertiaryContainer -> onTertiaryContainer
        ColorSchemeKeyTokens.Outline -> outline
        ColorSchemeKeyTokens.OutlineVariant -> outlineVariant
        ColorSchemeKeyTokens.Primary -> primary
        ColorSchemeKeyTokens.PrimaryContainer -> primaryContainer
        ColorSchemeKeyTokens.Scrim -> scrim
        ColorSchemeKeyTokens.Secondary -> secondary
        ColorSchemeKeyTokens.SecondaryContainer -> secondaryContainer
        ColorSchemeKeyTokens.Surface -> surface
        ColorSchemeKeyTokens.SurfaceVariant -> surfaceVariant
        ColorSchemeKeyTokens.SurfaceBright -> surfaceBright
        ColorSchemeKeyTokens.SurfaceContainer -> surfaceContainer
        ColorSchemeKeyTokens.SurfaceContainerHigh -> surfaceContainerHigh
        ColorSchemeKeyTokens.SurfaceContainerHighest -> surfaceContainerHighest
        ColorSchemeKeyTokens.SurfaceContainerLow -> surfaceContainerLow
        ColorSchemeKeyTokens.SurfaceContainerLowest -> surfaceContainerLowest
        ColorSchemeKeyTokens.SurfaceDim -> surfaceDim
        ColorSchemeKeyTokens.Tertiary -> tertiary
        ColorSchemeKeyTokens.TertiaryContainer -> tertiaryContainer
    }
}

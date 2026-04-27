package com.crypticsamsara.zelta.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically


// Easing curves
val ZeltaEaseOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
val ZeltaEaseInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
val ZeltaEaseSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness    = Spring.StiffnessLow
)

// screen transitions
val zeltaSlideInFromRight: EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(300, easing = ZeltaEaseOut)
    ) + fadeIn(animationSpec = tween(300))

val zeltaSlideOutToLeft: ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(300, easing = ZeltaEaseOut)
    ) + fadeOut(animationSpec = tween(200))

val zeltaSlideInFromLeft: EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec  = tween(300, easing = ZeltaEaseOut)
    ) + fadeIn(animationSpec = tween(300))

val zeltaSlideOutToRight: ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(300, easing = ZeltaEaseOut)
    ) + fadeOut(animationSpec = tween(200))

// sheet transitions
val zeltaSheetEnter: EnterTransition =
    slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(380, easing = ZeltaEaseOut)
    ) + fadeIn(animationSpec = tween(200))

val zeltaSheetExit: ExitTransition =
    slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(280, easing = ZeltaEaseInOut)
    ) + fadeOut(animationSpec = tween(200))

// card pop-in
val zeltaCardEnter: EnterTransition =
    scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(280, easing = ZeltaEaseOut)
    ) + fadeIn(animationSpec = tween(280))

val zeltaCardExit: ExitTransition =
    scaleOut(
        targetScale = 0.92f,
        animationSpec = tween(200)
    ) + fadeOut(animationSpec = tween(200))

// overlay fade
val zeltaFadeIn: EnterTransition =
    fadeIn(animationSpec = tween(250))

val zeltaFadeOut: ExitTransition =
    fadeOut(animationSpec = tween(200))

package com.matin.barbanet.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.matin.barbanet.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoadingScreen(
    loading: Boolean
) {
    val loadingDialogScreen = remember { mutableStateOf(loading) }
    if (!loading) return
    Dialog(
        onDismissRequest = { loadingDialogScreen.value },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingAnimation()


                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.loading),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.please_wait),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    letterSpacing = 3.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(24.dp))

            }
        }

    }
}

@Composable
fun LoadingAnimation(
    animationDelay: Int = 1000
) {
    val circles = listOf(
        remember {
            androidx.compose.animation.core.Animatable(initialValue = 0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(initialValue = 0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(0f)
        },
        remember {
            androidx.compose.animation.core.Animatable(0f)
        })

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = ((animationDelay) * (index + 1)).toLong())
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        delayMillis = animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .size(400.dp)
            .background(color = Color.Transparent)
    ) {
        circles.forEachIndexed { index, animatable ->
            Box(
                modifier = Modifier
                    .scale(animatable.value)
                    .size(400.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (index % 2 == 0) Color.Green.copy(alpha = 1 - animatable.value)
                        else Color.Yellow.copy(alpha = 1 - animatable.value)
                    )
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "logo in loading",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth()
            )

        }

    }


}
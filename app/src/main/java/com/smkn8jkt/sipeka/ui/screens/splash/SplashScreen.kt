package com.smkn8jkt.sipeka.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smkn8jkt.sipeka.R
import com.smkn8jkt.sipeka.data.remote.TokenManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    tokenManager: TokenManager? = null,
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500L)
        val token = tokenManager?.getTokenSync()
        if (!token.isNullOrBlank()) {
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.loading_sipeka),
            contentDescription = "Logo SIPeKa",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(180.dp)
        )
    }
}

package com.example.densitrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.densitrack.R
import com.example.densitrack.ui.theme.DensiTrackTheme

@Composable
fun LoadingPage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05445E))
    ){
        Image(
            painter = painterResource(id = R.drawable.ic_logo_white),
            contentDescription = "White Logo",
            modifier = Modifier
                .scale(2.25f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPagePreview() {
    DensiTrackTheme {
        LoadingPage()
    }
}
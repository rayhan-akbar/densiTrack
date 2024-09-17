package com.example.densitrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.densitrack.R
import com.example.densitrack.ui.theme.DensiTrackTheme

@Composable
fun MapPage() {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05445E))
            .padding(top = 88.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_densitracklogo),
                contentDescription = "White Logo",
                modifier = Modifier
                    .scale(0.9f)
            )
            Spacer(
                modifier = Modifier
                    .padding(16.dp)
            )
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF)
                ),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ){
                    Image(
                        painter = painterResource(id = R.drawable.ic_map),
                        contentDescription = "Map",
                        modifier = Modifier
                            .padding(vertical = 120.dp)
                            .padding(horizontal = 20.dp)
                            .scale(2.75f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapPagePreview() {
    DensiTrackTheme {
        MapPage()
    }
}
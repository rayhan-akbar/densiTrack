package com.example.densitrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.densitrack.R
import com.example.densitrack.ui.theme.DensiTrackTheme

@Composable
fun HomePage(navController: NavHostController) {
    val chivoLight = FontFamily(Font(R.font.chivo_light, weight = FontWeight.Light))
    val chivo = FontFamily(Font(R.font.chivo, weight = FontWeight.Normal))
    val chivoBold = FontFamily(Font(R.font.chivo_bold, weight = FontWeight.Bold))

    // State for selected items
    var selectedBusStop by remember { mutableStateOf("Stasiun UI") }
    var selectedDirection by remember { mutableStateOf("Blue") }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05445E))
            .padding(top = 88.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_densitracklogo),
                    contentDescription = "White Logo",
                    modifier = Modifier.scale(0.9f)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                    modifier = Modifier
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp).padding(top = 24.dp).padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier) {
                            Text(text = "Bus Stop", fontSize = 18.sp, modifier = Modifier)
                            Spacer(modifier = Modifier.height(8.dp))
                            BusStopMenu(selectedBusStop) { selectedBusStop = it }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Line", fontSize = 18.sp, modifier = Modifier)
                            Spacer(modifier = Modifier.height(8.dp))
                            DirectionMenu(selectedDirection) { selectedDirection = it }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF75E6DA)),
                            onClick = {
                                navController.navigate("details/${selectedBusStop}/${selectedDirection}")
                            },
                        ) {
                            Text(
                                text = "Search",
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 4.dp).padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Need a map?",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 4.dp).clickable {
                    navController.navigate("map")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BusStopMenu(selectedBusStop: String, onBusStopSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf(
        "Stasiun UI",
        "Asrama UI",
        "Menwa",
        "FPsi - Fakultas Psikologi",
        "FISIP - Fakultas Ilmu Sosial dan Ilmu Politik",
        "FIB - Fakultas Ilmu Budaya",
        "FEB - Fakultas Ekonomi dan Bisnis",
        "FT - Fakultas Teknik",
        "Vokasi",
        "SOR / PNJ",
        "FMIPA - Fakultas Matematika dan Ilmu Pengetahuan Alam",
        "FIK - Fakultas Ilmu Keperawatan",
        "FKM - Fakultas Kesehatan Masyarakat",
        "RIK - Rumpun Ilmu Kesehatan",
        "Balairung / Stasiun Pondok Cina",
        "Masjid UI / Perpustakaan UI",
        "FH - Fakultas Hukum"
    )
    val characterLimit = 25
    var selectedIndex by remember { mutableStateOf(0) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            enabled = false,
            value = truncateText(items[selectedIndex], characterLimit),
            onValueChange = { },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color(0xFF189AB4),
            ),
            modifier = Modifier.menuAnchor(),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedIndex = index
                        onBusStopSelected(item)
                        expanded = false
                    }
                )
                if (index < items.size - 1) {
                    Divider(color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectionMenu(selectedDirection: String, onDirectionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Blue", "Red")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            enabled = false,
            value = selectedDirection,
            onValueChange = { },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White,

                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color(0xFF189AB4),
            ),
            modifier = Modifier.menuAnchor(),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onDirectionSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun truncateText(text: String, limit: Int): String {
    return if (text.length > limit) {
        text.substring(0, limit) + "..."
    } else {
        text
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    DensiTrackTheme {
        HomePage(navController = rememberNavController())
    }
}
package com.example.densitrack.ui.screens

import android.app.VoiceInteractor
import android.util.Log
import android.view.WindowInsetsAnimation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.densitrack.R
import com.example.densitrack.ui.theme.DensiTrackTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


@Composable
fun DetailsPage(navController: NavHostController, busStop: String, direction: String) {
    val directionColor = if (direction.lowercase() == "biru") Color(0xFF189AB4) else Color(0xFFC10F0F)
    val truncatedBusStop = truncateDetailsText(busStop, 15)

    // Hardcoded values for busNumber and ETA
    val busNumber = "1234"  // Hardcoded bus number
    val eta = "ETA: Unknown"  // Hardcoded ETA

    // State for bus capacity (will be fetched from the backend)
    var busCapacity by remember { mutableStateOf("Fetching...") }

    // Trigger API call to get bus capacity
    LaunchedEffect(Unit) {
        fetchBusCapacity { capacity ->
            busCapacity = capacity
        }
    }

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
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_densitracklogo),
                contentDescription = "White Logo",
                modifier = Modifier.scale(0.9f)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Bus Heading to")
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF000000)),
                            modifier = Modifier.width(180.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = truncatedBusStop,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 4.dp).padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Direction")
                        Card(
                            colors = CardDefaults.cardColors(containerColor = directionColor),
                            modifier = Modifier.width(180.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = direction,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 4.dp).padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Display single bus info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    BusInfoRow(busNumber = busNumber, eta = eta, capacity = busCapacity)
                }
            }
        }
    }
}

@Composable
fun BusInfoRow(busNumber: String, eta: String, capacity: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bus_blue),
            contentDescription = "Blue Bus Icon",
            modifier = Modifier
                .scale(3f)
                .padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF05445E))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(2.dp).padding(horizontal = 8.dp)
                ) {
                    Text(text = "B", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = busNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "UI", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = eta, fontSize = 12.sp)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF000000)),
            modifier = Modifier.padding()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(6.dp)
            ) {
                Text(text = "Capacity", fontSize = 10.sp, color = Color.White)
                Text(text = capacity, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

fun truncateDetailsText(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.take(maxLength) + "..."
    } else {
        text
    }
}

// Function to fetch bus capacity (detected_object) from the backend
fun fetchBusCapacity(onResult: (String) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:3000/tr/getobject")  // Use 10.0.2.2 for emulator
        .build()

    // Make the network call asynchronously
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onResult("Error: ${e.message}")  // Provide more details about the error
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { body ->
                Log.d("API Response", body)

                try {
                    val json = JSONObject(body)
                    if (json.getString("message") == "Last detected object found") {
                        val detectedObjectString = json.getString("detected_object")
                        val detectedObject = detectedObjectString.toIntOrNull()  // Convert to int

                        if (detectedObject != null) {
                            val capacityPercentage = (detectedObject.toFloat() / 50) * 100
                            val formattedCapacity = String.format("%.0f%%", capacityPercentage)  // Format as percentage
                            onResult(formattedCapacity)
                        } else {
                            onResult("Error: Invalid capacity data")
                        }
                    } else {
                        val errorMessage = json.optString("error_message", "Unknown error")
                        onResult("Error: $errorMessage")
                    }
                } catch (e: Exception) {
                    Log.e("API Response", "Error parsing JSON", e)
                    onResult("Error: Failed to parse response")
                }
            } ?: run {
                onResult("Error: Empty response")
            }
        }

    })
}

@Preview(showBackground = true)
@Composable
fun DetailsPagePreview() {
    DetailsPage(navController = rememberNavController(), busStop = "Bus Stop", direction = "biru")
}
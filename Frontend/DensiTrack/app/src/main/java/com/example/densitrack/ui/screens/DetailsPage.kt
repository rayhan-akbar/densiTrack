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
import androidx.compose.foundation.layout.size
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

const val TOTAL_CAPACITY = 25
@Composable
fun DetailsPage(navController: NavHostController, busStop: String, direction: String) {
    val directionColor = if (direction.lowercase() == "blue") Color(0xFF189AB4) else Color(0xFFC10F0F)
    val truncatedBusStop = truncateDetailsText(busStop, 15)

    // Hardcoded values for busNumber and ETA
    val busNumber = "08"  // Hardcoded bus number
    val eta = "Unk"  // Hardcoded ETA

    // State for bus capacity (will be fetched from the backend)
    var frontCapacity by remember { mutableStateOf("...") }
    var backCapacity by remember { mutableStateOf("...") }
    var isRefreshing by remember { mutableStateOf(false) }

    // Trigger API call to get bus capacity
    LaunchedEffect(Unit) {
        fetchBusCapacity { front, back ->
            frontCapacity = calculatePercentage(front)  // Calculate front percentage
            backCapacity = calculatePercentage(back)    // Calculate back percentage
        }
    }

    // Swipe refresh state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05445E))
            .padding(top = 88.dp)
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                // Trigger a new API call to refresh the data
                fetchBusCapacity { front, back ->
                    frontCapacity = calculatePercentage(front)  // Recalculate front percentage
                    backCapacity = calculatePercentage(back)    // Recalculate back percentage
                    isRefreshing = false
                }
            }
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
                            Text(text = "Bus Heading to", fontSize = 16.sp)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF000000)),
                                modifier = Modifier.width(160.dp)
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
                            Text(text = "Line", fontSize = 16.sp)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = directionColor),
                                modifier = Modifier.width(160.dp)
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
                        BusInfoRow(busNumber = busNumber, eta = eta, frontCapacity = frontCapacity, backCapacity = backCapacity)
                    }
                }
            }
        }
    }
}

@Composable
fun BusInfoRow(busNumber: String, eta: String, frontCapacity: String, backCapacity: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF05445E))
            ) {
                Text(
                    text = busNumber,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Bus $busNumber", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Text(text = "ETA: $eta", fontSize = 16.sp, color = Color.Black)
            }
        }


        // Display front and back capacities
        Row {
            // Back capacity display
            Box(
                modifier = Modifier.size(80.dp)  // Adjust size as needed for both boxes
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back Bus Icon",
                    modifier = Modifier
                        .size(80.dp)  // Adjust size for icons
                        .align(Alignment.Center)
                )
                Text(
                    text = backCapacity,  // Back capacity percentage
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,  // Adjust font size as needed
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Front capacity display
            Box(
                modifier = Modifier.size(80.dp)  // Adjust size as needed for both boxes
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_front),
                    contentDescription = "Front Bus Icon",
                    modifier = Modifier
                        .size(80.dp)  // Adjust size for icons
                        .align(Alignment.Center)
                )
                Text(
                    text = frontCapacity,  // Front capacity percentage
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,  // Adjust font size as needed
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
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
fun fetchBusCapacity(onResult: (String, String) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:3000/tr/getobject")  // Use 10.0.2.2 for the emulator
        .build()

    // Make the network call asynchronously
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onResult("Error", "Error")  // Pass error values to both front and back
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { body ->
                Log.d("API Response", body)

                try {
                    val json = JSONObject(body)
                    if (json.getString("message") == "Last front and back values found") {
                        val front = json.getString("front")
                        val back = json.getString("back")
                        onResult(front, back)  // Correctly pass both front and back values
                    } else {
                        onResult("Error", "Error")
                    }
                } catch (e: Exception) {
                    Log.e("API Response", "Error parsing JSON", e)
                    onResult("Error", "Error")  // Handle JSON parsing error
                }
            } ?: run {
                onResult("Error", "Error")  // Handle empty response case
            }
        }
    })
}

// Function to calculate the percentage
fun calculatePercentage(value: String): String {
    return try {
        val intValue = value.toInt()
        val percentage = (intValue.toFloat() / TOTAL_CAPACITY) * 100
        "${percentage.toInt()}%"  // Convert to integer percentage and append "%"
    } catch (e: Exception) {
        "Error"  // Return "Error" if value is not a valid integer
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsPagePreview() {
    DetailsPage(navController = rememberNavController(), busStop = "Bus Stop", direction = "Blue")
}
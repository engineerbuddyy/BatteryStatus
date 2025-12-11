package com.example.batterypercentage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.example.batterypercentage.ui.theme.BatteryPercentageTheme
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryPercentageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BatteryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BatteryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val batteryPercentage = remember { mutableStateOf(0) }
    val batteryStatus = remember { mutableStateOf("Unknown") }

    DisposableEffect(context) {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                batteryPercentage.value = level
                batteryStatus.value = when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                    BatteryManager.BATTERY_STATUS_FULL -> "Full"
                    else -> "Unknown"
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(broadcastReceiver, filter)
        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    val batteryIcon = when (batteryStatus.value) {
        "Charging" -> R.drawable.charging
        "Discharging" -> R.drawable.discharge
        "Full" -> R.drawable.charged
        else -> R.drawable.charging
    }

    // Dynamic color based on battery level
    val batteryColor = when {
        batteryPercentage.value >= 80 -> Color(0xFF4CAF50) // Green
        batteryPercentage.value >= 50 -> Color(0xFF8BC34A) // Light Green
        batteryPercentage.value >= 20 -> Color(0xFFFFC107) // Amber
        else -> Color(0xFFF44336) // Red
    }

    val backgroundColor = when {
        batteryPercentage.value >= 80 -> Color(0xFFE8F5E9)
        batteryPercentage.value >= 50 -> Color(0xFFF1F8E9)
        batteryPercentage.value >= 20 -> Color(0xFFFFF9C4)
        else -> Color(0xFFFFEBEE)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                ) {
                    // Battery Icon
                    Surface(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(90.dp)),
                        color = batteryColor.copy(alpha = 0.1f)
                    ) {
                        Image(
                            painter = painterResource(id = batteryIcon),
                            contentDescription = "Battery Icon",
                            modifier = Modifier
                                .padding(30.dp)
                                .fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    Text(
                        text = "${batteryPercentage.value}%",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = batteryColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        color = batteryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = batteryStatus.value,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = batteryColor,
                            modifier = Modifier.padding(
                                horizontal = 24.dp,
                                vertical = 8.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    LinearProgressIndicator(
                        progress = { batteryPercentage.value / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = batteryColor,
                        trackColor = batteryColor.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                  
                    Text(
                        text = when {
                            batteryPercentage.value >= 80 -> "Battery is healthy"
                            batteryPercentage.value >= 20 -> "Battery level is good"
                            else -> "Please charge your device"
                        },
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
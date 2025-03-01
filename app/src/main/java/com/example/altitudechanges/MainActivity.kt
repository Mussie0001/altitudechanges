package com.example.altitudechanges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.altitudechanges.ui.theme.AltitudechangesTheme
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AltitudechangesTheme {
                ChangeInAltitude()
            }
        }
    }
}

@Composable
fun ChangeInAltitude() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }
    val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    var pressure by remember { mutableStateOf(1013.25f) }
    var altitude by remember { mutableStateOf(0f) }
    val altitudeColor = calculateBackgroundColor(altitude)

    // sensor listener
    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.firstOrNull()?.let {
                    pressure = it
                    altitude = calculateAltitude(it)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    // registers and unregister the sensor listener
    DisposableEffect(pressureSensor) {
        pressureSensor?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // UI portion
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(altitudeColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Altitude Changes", fontSize = 24.sp, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Pressure: ${"%.2f".format(pressure)} hPa", fontSize = 18.sp, color = Color.White)
        Text("Altitude: ${"%.2f".format(altitude)} meters", fontSize = 18.sp, color = Color.White)
    }
}

// formula to convert pressure readings into altitude
fun calculateAltitude(pressure: Float): Float {
    val P0 = 1013.25f
    return 44330 * (1 - (pressure / P0).pow(1 / 5.255f))
}

// darker at higher altitude, lighter at lower altitude
fun calculateBackgroundColor(altitude: Float): Color {
    return when {
        altitude < 500 -> Color(0xFF87CEEB)
        altitude < 1000 -> Color(0xFF4682B4)
        altitude < 1500 -> Color(0xFF2F4F4F)
        altitude < 2000 -> Color(0xFF4682B4)
        else -> Color(0xFF000000)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AltitudechangesTheme {
        ChangeInAltitude()
    }
}
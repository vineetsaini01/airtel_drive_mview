package com.visionairtel.drivetest.presentation.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visionairtel.drivetest.presentation.component.HSpacer


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),

    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Connected Wi-Fi Network",
                        color = Color.Gray
                    )
                    10.HSpacer()
                    CardTextItem("SSID", "AirFiber")
                    CardTextItem("RSSI", "-49dBm")
                    CardTextItem("Link Speed", "433Mbps")
                    CardTextItem("BSSID", "3a:3b:ab:18:d1:47")
                    CardTextItem("Frequency", "5785MHz (5.0GHz)")
                    CardTextItem("Channel", "157")
                    CardTextItem("Wi-Fi Standard", "5")

                }
            }

            20.HSpacer()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Device Details",
                        color = Color.Gray
                    )
                    10.HSpacer()
                    CardTextItem("Model", "SM-M146B")
                    CardTextItem("Android Sdk", "34")
                    CardTextItem("Android Version", "14")
                    CardTextItem("Battery", "65%")

                }
            }
        }
    }
}

@Composable
fun CardTextItem(key: String, value: String, fontSize: TextUnit = 14.sp) {
    Row {
        Text(text = key, modifier = Modifier.weight(1f), fontSize = fontSize)
        Text(text = value, modifier = Modifier.weight(1f), color = Color.Gray, fontSize = fontSize)
    }
}


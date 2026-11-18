package com.m.anakpintar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NekoApp()
        }
    }
}

@Composable
fun NekoApp() {
    val orange = Color(0xFFFFA726)
    val blue = Color(0xFF42A5F5)

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8E1))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🐱 M ANAK PINTAR",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF6C00),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Halo! Aku Neko. Ayo belajar bersama!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Belajar • Bermain • Tumbuh Bersama Neko",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(28.dp))

            MenuButton("🔤  Belajar Huruf", blue)
            MenuButton("🔢  Belajar Angka", Color(0xFF66BB6A))
            MenuButton("🐘  Mengenal Hewan", Color(0xFFAB47BC))
            MenuButton("🎨  Mengenal Warna", orange)
            MenuButton("🎮  Permainan Seru", Color(0xFFEC407A))
            MenuButton("📖  Cerita Anak", Color(0xFF26A69A))
            MenuButton("🏆  Nilai & Prestasi", Color(0xFF7E57C2))
        }
    }
}

@Composable
fun MenuButton(title: String, color: Color) {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

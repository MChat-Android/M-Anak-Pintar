package com.m.anakpintar

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

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
    var halaman by remember { mutableStateOf("beranda") }
    var indeksHuruf by remember { mutableStateOf(0) }

    val warnaLatar = Color(0xFFFFF8E1)

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = warnaLatar
        ) {
            if (halaman == "huruf") {
                BelajarHuruf(
                    indeks = indeksHuruf,
                    ubahIndeks = { indeksHuruf = it },
                    kembali = { halaman = "beranda" }
                )
            } else {
                Beranda(
                    bukaHuruf = {
                        indeksHuruf = 0
                        halaman = "huruf"
                    }
                )
            }
        }
    }
}

@Composable
fun Beranda(bukaHuruf: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🐱 M ANAK PINTAR",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF6C00),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Halo! Aku Neko. Ayo belajar bersama!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            "Belajar • Bermain • Tumbuh Bersama Neko",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(25.dp))

        MenuButton("🔤  Belajar Huruf", Color(0xFF42A5F5), bukaHuruf)
        MenuButton("🔢  Belajar Angka", Color(0xFF66BB6A))
        MenuButton("🐘  Mengenal Hewan", Color(0xFFAB47BC))
        MenuButton("🎨  Mengenal Warna", Color(0xFFFFA726))
        MenuButton("🎮  Permainan Seru", Color(0xFFEC407A))
        MenuButton("📖  Cerita Anak", Color(0xFF26A69A))
        MenuButton("🏆  Nilai & Prestasi", Color(0xFF7E57C2))
    }
}

@Composable
fun MenuButton(
    judul: String,
    warna: Color,
    aksi: () -> Unit = {}
) {
    Button(
        onClick = aksi,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = warna
        )
    ) {
        Text(
            judul,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BelajarHuruf(
    indeks: Int,
    ubahIndeks: (Int) -> Unit,
    kembali: () -> Unit
) {
    val daftarHuruf = listOf(
        "Ayam" to "🐔",
        "Bola" to "⚽",
        "Cicak" to "🦎",
        "Domba" to "🐑",
        "Elang" to "🦅",
        "Foto" to "📷",
        "Gajah" to "🐘",
        "Harimau" to "🐯",
        "Ikan" to "🐟",
        "Jeruk" to "🍊",
        "Kucing" to "🐱",
        "Lampu" to "💡",
        "Mobil" to "🚗",
        "Nanas" to "🍍",
        "Obat" to "💊",
        "Pisang" to "🍌",
        "Quran" to "📖",
        "Rusa" to "🦌",
        "Sapi" to "🐄",
        "Topi" to "🧢",
        "Ular" to "🐍",
        "Vas" to "🏺",
        "Wortel" to "🥕",
        "Xilofon" to "🎼",
        "Yoyo" to "🪀",
        "Zebra" to "🦓"
    )

    val huruf = ('A'..'Z').elementAt(indeks)
    val kata = daftarHuruf[indeks].first
    val emoji = daftarHuruf[indeks].second

    val konteks = LocalContext.current

    val suara = remember(konteks) {
        TextToSpeech(konteks, null)
    }

    DisposableEffect(suara) {
        onDispose {
            suara.stop()
            suara.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "🐱 BELAJAR HURUF",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF6C00)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Ayo kenali huruf bersama Neko!",
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    huruf.toString(),
                    fontSize = 105.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEF6C00)
                )

                Text(
                    "$huruf untuk $kata",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    emoji,
                    fontSize = 55.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                suara.language = Locale("id", "ID")
                suara.speak(
                    "$huruf, $kata",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "suara_huruf"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF26A69A)
            )
        ) {
            Text("🔊 Dengarkan Suara", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (indeks > 0) ubahIndeks(indeks - 1)
                },
                enabled = indeks > 0
            ) {
                Text("⬅ Sebelumnya")
            }

            Button(
                onClick = {
                    if (indeks < 25) ubahIndeks(indeks + 1)
                },
                enabled = indeks < 25
            ) {
                Text("Berikutnya ➡")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Huruf ${indeks + 1} dari 26",
            fontSize = 15.sp,
            color = Color.DarkGray
        )

        TextButton(onClick = kembali) {
            Text("🏠 Kembali ke Beranda", fontSize = 17.sp)
        }
    }
}

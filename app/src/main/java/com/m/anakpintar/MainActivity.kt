package com.m.anakpintar

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AnakPintarApp()
            }
        }
    }
}

private enum class Page {
    HOME, HURUF, ANGKA, BERHITUNG, WARNA, HEWAN,
    PERMAINAN, CERITA, PRESTASI
}

private data class MathQuestion(
    val first: Int,
    val second: Int,
    val symbol: String,
    val answer: Int,
    val choices: List<Int>
)

private data class LetterItem(val letter: String, val word: String)

private val letters = listOf(
    LetterItem("A", "Apel"), LetterItem("B", "Bola"),
    LetterItem("C", "Cicak"), LetterItem("D", "Domba"),
    LetterItem("E", "Elang"), LetterItem("F", "Foto"),
    LetterItem("G", "Gajah"), LetterItem("H", "Harimau"),
    LetterItem("I", "Ikan"), LetterItem("J", "Jeruk"),
    LetterItem("K", "Kucing"), LetterItem("L", "Lemon"),
    LetterItem("M", "Mobil"), LetterItem("N", "Nanas"),
    LetterItem("O", "Obat"), LetterItem("P", "Pisang"),
    LetterItem("Q", "Quran"), LetterItem("R", "Rumah"),
    LetterItem("S", "Sapi"), LetterItem("T", "Topi"),
    LetterItem("U", "Ular"), LetterItem("V", "Vas"),
    LetterItem("W", "Wortel"), LetterItem("X", "Xilofon"),
    LetterItem("Y", "Yoyo"), LetterItem("Z", "Zebra")
)

private fun numberWords(n: Int): String {
    val units = listOf(
        "nol", "satu", "dua", "tiga", "empat", "lima",
        "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas"
    )

    return when {
        n < 0 -> "minus ${numberWords(-n)}"
        n < 12 -> units[n]
        n < 20 -> "${numberWords(n - 10)} belas"
        n < 100 -> {
            val remainder = n % 10
            "${numberWords(n / 10)} puluh" +
                if (remainder > 0) " ${numberWords(remainder)}" else ""
        }
        n == 100 -> "seratus"
        else -> n.toString()
    }
}

private fun newMathQuestion(): MathQuestion {
    val addition = Random.nextBoolean()
    val first = Random.nextInt(1, 11)
    val second = if (addition) {
        Random.nextInt(1, 11)
    } else {
        Random.nextInt(1, first + 1)
    }

    val answer = if (addition) first + second else first - second
    val choices = (0..20).shuffled().take(3).toMutableList()
    choices.add(Random.nextInt(4), answer)

    return MathQuestion(
        first, second,
        if (addition) "+" else "−",
        answer,
        choices.distinct().let {
            if (it.size >= 4) it.take(4) else (it + (0..20).shuffled()).distinct().take(4)
        }.shuffled()
    )
}

private fun newLetterQuestion(): LetterItem = letters.random()

@Composable
private fun AnakPintarApp() {
    val context = LocalContext.current

    var speechEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var speechReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val engineReference = arrayOfNulls<TextToSpeech>(1)

        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engineReference[0]?.language = Locale("id", "ID")
                speechReady = true
            }
        }

        engineReference[0] = engine
        speechEngine = engine

        onDispose {
            speechReady = false
            engine.stop()
            engine.shutdown()
            speechEngine = null
        }
    }

    fun speak(text: String) {
        if (speechReady) {
            speechEngine?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "anak-pintar"
            )
        }
    }

    var page by rememberSaveable { mutableStateOf(Page.HOME) }
    var stars by rememberSaveable { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF8E8)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (page != Page.HOME) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF5B8DEF))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "← Kembali",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            page = Page.HOME
                        }
                    )
                    Spacer(Modifier.weight(1f))
                    Text("⭐ $stars", color = Color.White, fontSize = 18.sp)
                }
            }

            when (page) {
                Page.HOME -> HomeScreen(stars) { page = it }
                Page.HURUF -> LetterScreen(::speak)
                Page.ANGKA -> NumberScreen(::speak)
                Page.BERHITUNG -> MathScreen(
                    onSpeak = ::speak,
                    onCorrect = { stars++ }
                )
                Page.WARNA -> ColorScreen(::speak)
                Page.HEWAN -> AnimalScreen(::speak)
                Page.PERMAINAN -> GameScreen(
                    onSpeak = ::speak,
                    onCorrect = { stars++ }
                )
                Page.CERITA -> StoryScreen(::speak)
                Page.PRESTASI -> AwardScreen(stars)
            }
        }
    }
}

@Composable
private fun HomeScreen(stars: Int, onSelect: (Page) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        Text("🐱", fontSize = 65.sp)
        Text(
            "M ANAK PINTAR",
            fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3159A8)
        )
        Text(
            "Belajar sambil bermain!",
            fontSize = 17.sp,
            color = Color(0xFF666666)
        )

        Spacer(Modifier.height(14.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFE18B)
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "⭐ Bintang prestasi: $stars",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        MenuButton("🔤  Belajar Huruf", Color(0xFF72C6F5)) {
            onSelect(Page.HURUF)
        }
        MenuButton("🔢  Belajar Angka 1–100", Color(0xFFFFB86B)) {
            onSelect(Page.ANGKA)
        }
        MenuButton("➕  Belajar Berhitung", Color(0xFF8BD6A2)) {
            onSelect(Page.BERHITUNG)
        }
        MenuButton("🎨  Belajar Warna", Color(0xFFEAA6D4)) {
            onSelect(Page.WARNA)
        }
        MenuButton("🐘  Belajar Hewan", Color(0xFFB9A4F5)) {
            onSelect(Page.HEWAN)
        }
        MenuButton("🎮  Permainan", Color(0xFFFFD56A)) {
            onSelect(Page.PERMAINAN)
        }
        MenuButton("📖  Cerita Anak", Color(0xFF83D8D0)) {
            onSelect(Page.CERITA)
        }
        MenuButton("🏆  Nilai dan Prestasi", Color(0xFFFFA6A6)) {
            onSelect(Page.PRESTASI)
        }

        Spacer(Modifier.height(20.dp))
        Text("Ayo belajar bersama Neko! 🐱", color = Color.Gray)
    }
}

@Composable
private fun MenuButton(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(
            title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF26334A)
        )
    }
}

@Composable
private fun PageHeading(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, fontSize = 15.sp, color = Color.Gray)
    }
}

@Composable
private fun SpeechButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4779D5)
        )
    ) {
        Text("🔊 $text", fontSize = 17.sp)
    }
}

@Composable
private fun LetterScreen(speak: (String) -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val item = letters[index]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PageHeading("Belajar Huruf", "Kenali huruf A sampai Z")

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFBCE7FF)
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(item.letter, fontSize = 105.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${item.letter} untuk ${item.word}",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                SpeechButton("Dengarkan") {
                    speak("${item.letter}, untuk ${item.word}")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                index = if (index == 0) 25 else index - 1
            }) {
                Text("Sebelumnya")
            }
            Button(onClick = {
                index = if (index == 25) 0 else index + 1
            }) {
                Text("Berikutnya")
            }
        }

        Text("${index + 1} dari 26 huruf", modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun NumberScreen(speak: (String) -> Unit) {
    var number by rememberSaveable { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PageHeading("Belajar Angka", "Kenali angka 1 sampai 100")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFDDAA)
            )
        ) {
            Column(
                modifier = Modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(number.toString(), fontSize = 95.sp, fontWeight = FontWeight.Bold)
                Text(numberWords(number), fontSize = 27.sp)
                Spacer(Modifier.height(14.dp))
                SpeechButton("Dengarkan") {
                    speak(numberWords(number))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(
                enabled = number > 1,
                onClick = { number-- }
            ) {
                Text("Sebelumnya")
            }
            Button(
                enabled = number < 100,
                onClick = { number++ }
            ) {
                Text("Berikutnya")
            }
        }

        Text("Angka $number dari 100", modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun MathScreen(
    onSpeak: (String) -> Unit,
    onCorrect: () -> Unit
) {
    var question by remember { mutableStateOf(newMathQuestion()) }
    var message by remember { mutableStateOf("Pilih jawaban yang benar!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PageHeading("Belajar Berhitung", "Ayo berlatih matematika!")

        Text(
            "${question.first} ${question.symbol} ${question.second} = ?",
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3159A8)
        )

        Spacer(Modifier.height(18.dp))

        question.choices.forEach { choice ->
            Button(
                onClick = {
                    if (choice == question.answer) {
                        message = "Hebat! Jawabanmu benar! ⭐"
                        onCorrect()
                        onSpeak("Hebat! Jawabanmu benar!")
                        question = newMathQuestion()
                    } else {
                        message = "Belum tepat. Coba lagi ya!"
                        onSpeak("Coba lagi ya")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Text(choice.toString(), fontSize = 23.sp)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(message, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ColorScreen(speak: (String) -> Unit) {
    val colors = listOf(
        Triple("Merah", "🔴", Color(0xFFE53935)),
        Triple("Biru", "🔵", Color(0xFF1976D2)),
        Triple("Kuning", "🟡", Color(0xFFFFD600)),
        Triple("Hijau", "🟢", Color(0xFF43A047)),
        Triple("Ungu", "🟣", Color(0xFF8E24AA)),
        Triple("Jingga", "🟠", Color(0xFFEF6C00)),
        Triple("Merah muda", "🌸", Color(0xFFF48FB1)),
        Triple("Cokelat", "🟤", Color(0xFF795548)),
        Triple("Hitam", "⚫", Color(0xFF212121)),
        Triple("Putih", "⚪", Color(0xFFFFFFFF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Belajar Warna", "Tekan warna untuk mendengarkan namanya")

        colors.forEach { (name, emoji, color) ->
            val textColor = if (
                name == "Putih" || name == "Kuning" || name == "Merah muda"
            ) Color.Black else Color.White

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { speak(name) },
                colors = CardDefaults.cardColors(containerColor = color),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 30.sp)
                    Spacer(Modifier.width(15.dp))
                    Text(
                        name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(Modifier.weight(1f))
                    Text("🔊", fontSize = 23.sp)
                }
            }
        }
    }
}

@Composable
private fun AnimalScreen(speak: (String) -> Unit) {
    val animals = listOf(
        "🐱" to "Kucing",
        "🐶" to "Anjing",
        "🐘" to "Gajah",
        "🦁" to "Singa",
        "🐯" to "Harimau",
        "🐮" to "Sapi",
        "🐔" to "Ayam",
        "🦆" to "Bebek",
        "🐟" to "Ikan",
        "🐸" to "Katak",
        "🐵" to "Monyet",
        "🐰" to "Kelinci",
        "🐼" to "Panda",
        "🦋" to "Kupu-kupu",
        "🐢" to "Kura-kura",
        "🦒" to "Jerapah"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Belajar Hewan", "Kenali nama-nama hewan")

        animals.forEach { (emoji, name) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { speak(name) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD8F1E0)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 36.sp)
                    Spacer(Modifier.width(18.dp))
                    Text(name, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text("🔊", fontSize = 23.sp)
                }
            }
        }
    }
}

@Composable
private fun GameScreen(
    onSpeak: (String) -> Unit,
    onCorrect: () -> Unit
) {
    var question by remember { mutableStateOf(newLetterQuestion()) }
    var message by remember { mutableStateOf("Ayo tebak hurufnya!") }

    val choices = remember(question) {
        (letters.filter { it.letter != question.letter }
            .shuffled()
            .take(3) + question).shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PageHeading("Permainan Tebak Huruf", "Pilih huruf yang benar!")

        Text(
            "${question.word} dimulai dengan huruf apa?",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        choices.forEach { choice ->
            Button(
                onClick = {
                    if (choice.letter == question.letter) {
                        message = "Benar! Kamu pintar! ⭐"
                        onCorrect()
                        onSpeak("Benar! ${question.word} dimulai dengan huruf ${question.letter}")
                        question = newLetterQuestion()
                    } else {
                        message = "Belum benar. Coba lagi!"
                        onSpeak("Coba lagi")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Text(choice.letter, fontSize = 26.sp)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(message, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StoryScreen(speak: (String) -> Unit) {
    val stories = listOf(
        "Neko dan Kupu-Kupu" to
            "Pada suatu pagi, Neko bermain di taman. " +
            "Ia melihat kupu-kupu yang cantik. " +
            "Neko tidak menangkapnya. Ia membiarkan kupu-kupu terbang bebas. " +
            "Neko senang karena bisa menyayangi makhluk hidup.",

        "Anak yang Rajin" to
            "Dina bangun pagi. Ia merapikan tempat tidur dan membantu ibu. " +
            "Setelah itu Dina belajar membaca. " +
            "Ibu memuji Dina karena rajin dan suka membantu. " +
            "Dina merasa bahagia."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Cerita Anak", "Dengarkan cerita yang menyenangkan")

        stories.forEach { (title, story) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 9.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDDF2FF)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text(story, fontSize = 17.sp, lineHeight = 25.sp)
                    Spacer(Modifier.height(12.dp))
                    SpeechButton("Dengarkan cerita") {
                        speak(story)
                    }
                }
            }
        }
    }
}

@Composable
private fun AwardScreen(stars: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏆", fontSize = 90.sp)
        Text(
            "Prestasi Belajar",
            fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(18.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFE18B)
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⭐", fontSize = 55.sp)
                Text(
                    "$stars Bintang",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    when {
                        stars >= 20 -> "Luar biasa! Kamu hebat!"
                        stars >= 10 -> "Hebat! Terus belajar!"
                        stars >= 5 -> "Bagus! Terus berlatih!"
                        else -> "Ayo bermain dan kumpulkan bintang!"
                    },
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

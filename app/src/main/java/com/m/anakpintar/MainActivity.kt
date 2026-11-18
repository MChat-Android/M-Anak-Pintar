
package com.m.anakpintar

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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

private data class LetterItem(val letter: String, val word: String)

private data class MathQuestion(
    val first: Int,
    val second: Int,
    val symbol: String,
    val answer: Int,
    val choices: List<Int>
)

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

private fun letterIllustration(word: String): String = when (word) {
    "Apel" -> "🍎"
    "Bola" -> "⚽"
    "Cicak" -> "🦎"
    "Domba" -> "🐑"
    "Elang" -> "🦅"
    "Foto" -> "📷"
    "Gajah" -> "🐘"
    "Harimau" -> "🐯"
    "Ikan" -> "🐟"
    "Jeruk" -> "🍊"
    "Kucing" -> "🐱"
    "Lemon" -> "🍋"
    "Mobil" -> "🚗"
    "Nanas" -> "🍍"
    "Obat" -> "💊"
    "Pisang" -> "🍌"
    "Quran" -> "📖"
    "Rumah" -> "🏠"
    "Sapi" -> "🐄"
    "Topi" -> "🧢"
    "Ular" -> "🐍"
    "Vas" -> "🏺"
    "Wortel" -> "🥕"
    "Xilofon" -> "🎼"
    "Yoyo" -> "🪀"
    "Zebra" -> "🦓"
    else -> "🌟"
}

private fun numberIllustration(number: Int): String = when (number % 10) {
    1 -> "🍎"
    2 -> "🧸"
    3 -> "🎈"
    4 -> "🚗"
    5 -> "⭐"
    6 -> "🐟"
    7 -> "🦋"
    8 -> "🍊"
    9 -> "⚽"
    else -> "🐱"
}

private fun numberWords(n: Int): String {
    val units = listOf(
        "nol", "satu", "dua", "tiga", "empat", "lima",
        "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas"
    )
    return when {
        n < 12 -> units[n]
        n < 20 -> "${numberWords(n - 10)} belas"
        n < 100 -> "${numberWords(n / 10)} puluh" +
            if (n % 10 > 0) " ${numberWords(n % 10)}" else ""
        n == 100 -> "seratus"
        else -> n.toString()
    }
}

private fun newMathQuestion(): MathQuestion {
    val addition = Random.nextBoolean()
    val a = Random.nextInt(1, 11)
    val b = if (addition) Random.nextInt(1, 11) else Random.nextInt(1, a + 1)
    val answer = if (addition) a + b else a - b
    val choices = ((0..20).shuffled().filter { it != answer }.take(3) + answer).shuffled()
    return MathQuestion(a, b, if (addition) "+" else "−", answer, choices)
}

@Composable
private fun AnakPintarApp() {
    val context = LocalContext.current
    var speechEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var speechReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engineLanguage(speechEngine)
                speechReady = true
            }
        }
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
            speechEngine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "anak-pintar")
        }
    }

    var page by rememberSaveable { mutableStateOf(Page.HOME) }
    var stars by rememberSaveable { mutableIntStateOf(0) }

    Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    when (page) {
                        Page.HOME -> listOf(
                            Color(0xFF80D8FF),
                            Color(0xFFE1F5FE),
                            Color(0xFFFFF3D6)
                        )
                        Page.HURUF -> listOf(
                            Color(0xFFB9F6CA),
                            Color(0xFFE8F5E9)
                        )
                        Page.ANGKA -> listOf(
                            Color(0xFFBBDEFB),
                            Color(0xFFFFE0B2)
                        )
                        Page.BERHITUNG -> listOf(
                            Color(0xFFA5D6A7),
                            Color(0xFFE8F5E9)
                        )
                        Page.WARNA -> listOf(
                            Color(0xFFF8BBD0),
                            Color(0xFFE1BEE7)
                        )
                        Page.HEWAN -> listOf(
                            Color(0xFFC8E6C9),
                            Color(0xFFFFF9C4)
                        )
                        Page.PERMAINAN -> listOf(
                            Color(0xFFFFE082),
                            Color(0xFFFFCCBC)
                        )
                        Page.CERITA -> listOf(
                            Color(0xFFB3E5FC),
                            Color(0xFFD1C4E9)
                        )
                        Page.PRESTASI -> listOf(
                            Color(0xFFFFE082),
                            Color(0xFFFFF8E1)
                        )
                    }
                )
            )
        ) {
            Column(Modifier.fillMaxSize()) {
                if (page != Page.HOME) {
                    Row(
                        Modifier.fillMaxWidth()
                            .background(Color(0xFF5B8DEF))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "← Kembali",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { page = Page.HOME }
                        )
                        Spacer(Modifier.weight(1f))
                        Text("⭐ $stars", color = Color.White, fontSize = 18.sp)
                    }
                }

                when (page) {
                    Page.HOME -> HomeScreen(stars) { page = it }
                    Page.HURUF -> LetterScreen(::speak)
                    Page.ANGKA -> NumberScreen(::speak)
                    Page.BERHITUNG -> MathScreen(::speak) { stars++ }
                    Page.WARNA -> ColorScreen(::speak)
                    Page.HEWAN -> AnimalScreen(::speak)
                    Page.PERMAINAN -> GameScreen(::speak) { stars++ }
                    Page.CERITA -> StoryScreen(::speak)
                    Page.PRESTASI -> AwardScreen(stars)
                }
            }
        }
    }
}

private fun engineLanguage(engine: TextToSpeech?) {
    engine?.language = Locale("id", "ID")
}

@Composable
private fun HomeScreen(stars: Int, onSelect: (Page) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Text("🐱", fontSize = 65.sp)
        Text("M ANAK PINTAR", fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold, color = Color(0xFF3159A8))
        Text("Belajar sambil bermain!", fontSize = 17.sp, color = Color.Gray)
        Spacer(Modifier.height(14.dp))
        MenuButton("⭐ Bintang prestasi: $stars", Color(0xFFFFE18B)) {}
        Spacer(Modifier.height(12.dp))
        MenuButton("🔤  Belajar Huruf", Color(0xFF72C6F5)) { onSelect(Page.HURUF) }
        MenuButton("🔢  Belajar Angka 1–100", Color(0xFFFFB86B)) { onSelect(Page.ANGKA) }
        MenuButton("➕  Belajar Berhitung", Color(0xFF8BD6A2)) { onSelect(Page.BERHITUNG) }
        MenuButton("🎨  Belajar Warna", Color(0xFFEAA6D4)) { onSelect(Page.WARNA) }
        MenuButton("🐘  Belajar Hewan", Color(0xFFB9A4F5)) { onSelect(Page.HEWAN) }
        MenuButton("🎮  Permainan", Color(0xFFFFD56A)) { onSelect(Page.PERMAINAN) }
        MenuButton("📖  Cerita Anak", Color(0xFF83D8D0)) { onSelect(Page.CERITA) }
        MenuButton("🏆  Nilai dan Prestasi", Color(0xFFFFA6A6)) { onSelect(Page.PRESTASI) }
        Spacer(Modifier.height(20.dp))
        Text("Ayo belajar bersama Neko! 🐱", color = Color.Gray)
    }
}

@Composable
private fun MenuButton(title: String, color: Color, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(title, Modifier.fillMaxWidth().padding(17.dp),
            fontSize = 19.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF26334A))
    }
}

@Composable
private fun PageHeading(title: String, subtitle: String) {
    Column(
        Modifier.fillMaxWidth().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, fontSize = 15.sp, color = Color.Gray)
    }
}

@Composable
private fun LearningIllustration(emoji: String, caption: String, background: Color) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 43.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(caption, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF3159A8), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SpeechButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4779D5))) {
        Text("🔊 $text", fontSize = 17.sp)
    }
}

@Composable
private fun LetterScreen(speak: (String) -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val item = letters[index]

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBCE7FF)),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(item.letter, fontSize = 105.sp, fontWeight = FontWeight.Bold)
            Text(
                "${item.letter} untuk ${item.word}",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                letterIllustration(item.word),
                fontSize = 92.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            SpeechButton("Dengarkan") {
                speak("${item.letter}, untuk ${item.word}")
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            Text("${index + 1} dari 26 huruf", Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun NumberScreen(speak: (String) -> Unit) {
    var number by rememberSaveable { mutableIntStateOf(1) }
    val word = numberWords(number)

    Card(
        Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDDAA))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(number.toString(), fontSize = 95.sp, fontWeight = FontWeight.Bold)
            Text(word, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text(numberIllustration(number), fontSize = 65.sp)
            Spacer(Modifier.height(12.dp))
            SpeechButton("Dengarkan") {
                speak(word)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            Text("Angka $number dari 100", Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun MathObjectGroup(count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF1D6)
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "$count apel",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            (1..count).toList().chunked(5).forEach { group ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    group.forEach {
                        Text("🍎", fontSize = 27.sp)
                        Spacer(Modifier.width(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MathScreen(onSpeak: (String) -> Unit, onCorrect: () -> Unit) {
    var question by remember { mutableStateOf(newMathQuestion()) }
    var message by remember { mutableStateOf("Pilih jawaban yang benar!") }

    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Belajar Berhitung", "Ayo berlatih matematika!")
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MathObjectGroup(question.first, Modifier.weight(1f))
            Text(question.symbol, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            MathObjectGroup(question.second, Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "${question.first} ${question.symbol} ${question.second} = ?",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3159A8),
            textAlign = TextAlign.Center
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
                Modifier.fillMaxWidth().padding(vertical = 5.dp)
            ) {
                Text(choice.toString(), fontSize = 23.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(message, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

private data class ColorItem(val name: String, val color: Color)

@Composable
private fun ColorScreen(speak: (String) -> Unit) {
    val colors = listOf(
        ColorItem("Merah", Color(0xFFE53935)),
        ColorItem("Biru", Color(0xFF1976D2)),
        ColorItem("Kuning", Color(0xFFFFD600)),
        ColorItem("Hijau", Color(0xFF43A047)),
        ColorItem("Ungu", Color(0xFF8E24AA)),
        ColorItem("Jingga", Color(0xFFEF6C00)),
        ColorItem("Merah muda", Color(0xFFF48FB1)),
        ColorItem("Cokelat", Color(0xFF795548)),
        ColorItem("Hitam", Color(0xFF212121)),
        ColorItem("Putih", Color.White)
    )
    var index by rememberSaveable { mutableIntStateOf(0) }
    val item = colors[index]
    Card(
        Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3DDF0))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(item.name, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(22.dp))
            Box(
                Modifier.size(190.dp).background(item.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (item.name == "Putih") {
                    Text("PUTIH", color = Color.Gray, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(22.dp))
            SpeechButton("Dengarkan") { speak(item.name) }
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    enabled = index > 0,
                    onClick = { index-- },
                    modifier = Modifier.weight(1f)
                ) { Text("Sebelumnya") }
                Button(
                    enabled = index < colors.lastIndex,
                    onClick = { index++ },
                    modifier = Modifier.weight(1f)
                ) { Text("Berikutnya") }
            }
            Text("Warna ${index + 1} dari ${colors.size}",
                Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun AnimalScreen(speak: (String) -> Unit) {
    val animals = listOf(
        "🐱" to "Kucing", "🐶" to "Anjing", "🐘" to "Gajah",
        "🦁" to "Singa", "🐯" to "Harimau", "🐮" to "Sapi",
        "🐔" to "Ayam", "🦆" to "Bebek", "🐟" to "Ikan",
        "🐸" to "Katak", "🐵" to "Monyet", "🐰" to "Kelinci",
        "🐼" to "Panda", "🦋" to "Kupu-kupu", "🐢" to "Kura-kura",
        "🦒" to "Jerapah"
    )
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Belajar Hewan", "Kenali nama-nama hewan")
        animals.forEach { (emoji, name) ->
            Card(
                Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { speak(name) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD8F1E0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
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
private fun GameScreen(onSpeak: (String) -> Unit, onCorrect: () -> Unit) {
    var question by remember { mutableStateOf(letters.random()) }
    var message by remember { mutableStateOf("Ayo tebak hurufnya!") }
    val choices = remember(question) {
        (letters.filter { it.letter != question.letter }.shuffled().take(3) + question).shuffled()
    }

    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Permainan Tebak Huruf", "Belajar bersama Neko!")

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF246B45)
                )
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "PAPAN PERMAINAN",
                        color = Color(0xFFFFE082),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${question.word} dimulai dengan huruf apa?",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 25.sp
                    )
                    Spacer(Modifier.height(14.dp))

                    choices.chunked(2).forEach { rowChoices ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowChoices.forEach { choice ->
                                Button(
                                    onClick = {
                                        if (choice.letter == question.letter) {
                                            message = "Benar! Kamu pintar! ⭐"
                                            onCorrect()
                                            onSpeak("Benar! ${question.word} dimulai dengan huruf ${question.letter}")
                                            question = letters.random()
                                        } else {
                                            message = "Belum benar. Coba lagi!"
                                            onSpeak("Coba lagi")
                                        }
                                    },
                                    modifier = Modifier.weight(1f).padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD166),
                                        contentColor = Color(0xFF26334A)
                                    )
                                ) {
                                    Text(
                                        choice.letter,
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        message,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.width(76.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🐱", fontSize = 55.sp)
                Text(
                    "Neko",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3159A8)
                )
                Text(
                    "Ayo jawab!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3159A8),
                    textAlign = TextAlign.Center
                )
                Text("👉", fontSize = 27.sp)
            }
        }
    }
}

@Composable
private fun StoryScreen(speak: (String) -> Unit) {
    val stories = listOf(
        "Neko dan Kupu-Kupu" to
            "Pada suatu pagi, Neko bermain di taman. Ia melihat kupu-kupu yang cantik. Neko tidak menangkapnya. Ia membiarkan kupu-kupu terbang bebas. Neko senang karena bisa menyayangi makhluk hidup.",
        "Anak yang Rajin" to
            "Dina bangun pagi. Ia merapikan tempat tidur dan membantu ibu. Setelah itu Dina belajar membaca. Ibu memuji Dina karena rajin dan suka membantu. Dina merasa bahagia."
    )
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Cerita Anak", "Dengarkan cerita yang menyenangkan")
        stories.forEach { (title, story) ->
            Card(
                Modifier.fillMaxWidth().padding(vertical = 9.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDDF2FF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text(story, fontSize = 17.sp, lineHeight = 25.sp)
                    Spacer(Modifier.height(12.dp))
                    SpeechButton("Dengarkan cerita") { speak(story) }
                }
            }
        }
    }
}

@Composable
private fun AwardScreen(stars: Int) {
    Column(
        Modifier.fillMaxSize().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏆", fontSize = 90.sp)
        Text("Prestasi Belajar", fontSize = 29.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE18B)),
            shape = RoundedCornerShape(25.dp)) {
            Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐", fontSize = 55.sp)
                Text("$stars Bintang", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(
                    when {
                        stars >= 20 -> "Luar biasa! Kamu hebat!"
                        stars >= 10 -> "Hebat! Terus belajar!"
                        stars >= 5 -> "Bagus! Terus berlatih!"
                        else -> "Ayo bermain dan kumpulkan bintang!"
                    },
                    fontSize = 17.sp, textAlign = TextAlign.Center
                )
            }
        }
    }
}

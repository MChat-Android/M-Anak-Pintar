
package com.m.anakpintar

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.*
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
    PERMAINAN, HIJAIYAH, PRESTASI
}

private data class LetterItem(val letter: String, val word: String)

private data class MathQuestion(
    val first: Int,
    val second: Int,
    val symbol: String,
    val answer: Int,
    val choices: List<Int>
)

private data class HijaiyahItem(val letter: String, val name: String)

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

private val hijaiyahLetters = listOf(
    HijaiyahItem("ا", "Alif"),
    HijaiyahItem("ب", "Ba"),
    HijaiyahItem("ت", "Ta"),
    HijaiyahItem("ث", "Tsa"),
    HijaiyahItem("ج", "Jim"),
    HijaiyahItem("ح", "Ha"),
    HijaiyahItem("خ", "Kha"),
    HijaiyahItem("د", "Dal"),
    HijaiyahItem("ذ", "Dzal"),
    HijaiyahItem("ر", "Ra"),
    HijaiyahItem("ز", "Zai"),
    HijaiyahItem("س", "Sin"),
    HijaiyahItem("ش", "Syin"),
    HijaiyahItem("ص", "Shad"),
    HijaiyahItem("ض", "Dhad"),
    HijaiyahItem("ط", "Tha"),
    HijaiyahItem("ظ", "Zha"),
    HijaiyahItem("ع", "Ain"),
    HijaiyahItem("غ", "Ghain"),
    HijaiyahItem("ف", "Fa"),
    HijaiyahItem("ق", "Qaf"),
    HijaiyahItem("ك", "Kaf"),
    HijaiyahItem("ل", "Lam"),
    HijaiyahItem("م", "Mim"),
    HijaiyahItem("ن", "Nun"),
    HijaiyahItem("و", "Wau"),
    HijaiyahItem("ه", "Ha"),
    HijaiyahItem("ء", "Hamzah"),
    HijaiyahItem("ي", "Ya")
)

private fun letterIllustration(word: String): String = when (word) {
    "Apel" -> "🍎"; "Bola" -> "⚽"; "Cicak" -> "🦎"
    "Domba" -> "🐑"; "Elang" -> "🦅"; "Foto" -> "📷"
    "Gajah" -> "🐘"; "Harimau" -> "🐯"; "Ikan" -> "🐟"
    "Jeruk" -> "🍊"; "Kucing" -> "🐱"; "Lemon" -> "🍋"
    "Mobil" -> "🚗"; "Nanas" -> "🍍"; "Obat" -> "💊"
    "Pisang" -> "🍌"; "Quran" -> "📖"; "Rumah" -> "🏠"
    "Sapi" -> "🐄"; "Topi" -> "🧢"; "Ular" -> "🐍"
    "Vas" -> "🏺"; "Wortel" -> "🥕"; "Xilofon" -> "🎼"
    "Yoyo" -> "🪀"; "Zebra" -> "🦓"
    else -> "🌟"
}

private fun numberIllustration(number: Int): String = when (number % 10) {
    1 -> "🍎"; 2 -> "🧸"; 3 -> "🎈"; 4 -> "🚗"; 5 -> "⭐"
    6 -> "🐟"; 7 -> "🦋"; 8 -> "🍊"; 9 -> "⚽"; else -> "🐱"
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
                speechEngine?.language = Locale("id", "ID")
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

    BackHandler(enabled = page != Page.HOME) {
        page = Page.HOME
    }

    val background = when (page) {
        Page.HOME -> listOf(Color(0xFF63C9F4), Color(0xFFB8ECFF), Color(0xFFFFF1C5))
        Page.HURUF -> listOf(Color(0xFFB9F6CA), Color(0xFFE8F5E9))
        Page.ANGKA -> listOf(Color(0xFFBBDEFB), Color(0xFFFFE0B2))
        Page.BERHITUNG -> listOf(Color(0xFFA5D6A7), Color(0xFFE8F5E9))
        Page.WARNA -> listOf(Color(0xFFF8BBD0), Color(0xFFE1BEE7))
        Page.HEWAN -> listOf(Color(0xFFC8E6C9), Color(0xFFFFF9C4))
        Page.PERMAINAN -> listOf(Color(0xFFFFE082), Color(0xFFFFCCBC))
        Page.HIJAIYAH -> listOf(Color(0xFFB3E5FC), Color(0xFFD1C4E9))
        Page.PRESTASI -> listOf(Color(0xFFFFE082), Color(0xFFFFF8E1))
    }

    Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(background))) {
            Column(
                Modifier.fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                when (page) {
                    Page.HOME -> HomeScreen(stars) { page = it }
                    Page.HURUF -> LetterScreen(::speak)
                    Page.ANGKA -> NumberScreen(::speak)
                    Page.BERHITUNG -> MathScreen(::speak) { stars++ }
                    Page.WARNA -> ColorScreen(::speak)
                    Page.HEWAN -> AnimalScreen(::speak)
                    Page.PERMAINAN -> GameScreen(::speak) { stars++ }
                    Page.HIJAIYAH -> HijaiyahScreen(::speak)
                    Page.PRESTASI -> AwardScreen(stars)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(stars: Int, onSelect: (Page) -> Unit) {
    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 13.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🐾", fontSize = 27.sp)
            Spacer(Modifier.width(7.dp))
            Text("M ANAK PINTAR", fontSize = 26.sp,
                fontWeight = FontWeight.Black, color = Color(0xFF174A91))
            Spacer(Modifier.width(7.dp))
            Text("⭐", fontSize = 25.sp)
        }

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth().shadow(
                10.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF2365A0)
            ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(Modifier.background(Brush.linearGradient(
                listOf(Color.White, Color(0xFFB5E9FF), Color(0xFF71C9F5))
            ))) {
                Row(
                    Modifier.fillMaxWidth().padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(112.dp).shadow(8.dp, CircleShape)
                            .background(
                                Brush.verticalGradient(listOf(Color.White, Color(0xFFFFD8A4))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐱", fontSize = 76.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Halo, Teman!", fontSize = 23.sp,
                            fontWeight = FontWeight.Black, color = Color(0xFF174A91))
                        Text("Aku Neko!", fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold, color = Color(0xFFE87525))
                        Text("Ayo belajar sambil bermain!", fontSize = 13.sp,
                            color = Color(0xFF315078), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text("🌈 🎈 ⭐", fontSize = 22.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(20.dp))
                .clickable { onSelect(Page.PRESTASI) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(
                        listOf(Color(0xFFFFF2B4), Color(0xFFFFCF55))
                    )).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("🏆", fontSize = 28.sp)
                Spacer(Modifier.width(10.dp))
                Text("Bintang prestasi: $stars", fontSize = 19.sp,
                    fontWeight = FontWeight.Black, color = Color(0xFF704214))
                Spacer(Modifier.width(8.dp))
                Text("⭐", fontSize = 23.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("PILIH KEGIATAN BELAJAR", fontSize = 17.sp,
            fontWeight = FontWeight.Black, color = Color(0xFF174A91))
        Spacer(Modifier.height(11.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HomeTile("🔤", "Belajar\nHuruf", Color(0xFFFF9BCB), Color(0xFFE95B9E),
                Modifier.weight(1f)) { onSelect(Page.HURUF) }
            HomeTile("🔢", "Belajar\nAngka", Color(0xFFB4F18C), Color(0xFF63BD55),
                Modifier.weight(1f)) { onSelect(Page.ANGKA) }
            HomeTile("➕", "Berhitung", Color(0xFFFFD477), Color(0xFFE99B24),
                Modifier.weight(1f)) { onSelect(Page.BERHITUNG) }
        }

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HomeTile("🎨", "Belajar\nWarna", Color(0xFFD1B8FF), Color(0xFF9670DF),
                Modifier.weight(1f)) { onSelect(Page.WARNA) }
            HomeTile("🐘", "Belajar\nHewan", Color(0xFF9DEDB4), Color(0xFF4CB978),
                Modifier.weight(1f)) { onSelect(Page.HEWAN) }
            HomeTile("🎮", "Permainan", Color(0xFF91DFFF), Color(0xFF419ED5),
                Modifier.weight(1f)) { onSelect(Page.PERMAINAN) }
        }

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HomeTile("🕌", "Belajar\nHijaiyah", Color(0xFFB8E6FF), Color(0xFF559AD9),
                Modifier.weight(1f)) { onSelect(Page.HIJAIYAH) }
            HomeTile("🏆", "Nilai dan\nPrestasi", Color(0xFFFFB3B3), Color(0xFFE56F78),
                Modifier.weight(1f)) { onSelect(Page.PRESTASI) }
            HomeTile("⭐", "Bintang\nSaya", Color(0xFFFFE58B), Color(0xFFE7B83B),
                Modifier.weight(1f)) { onSelect(Page.PRESTASI) }
        }

        Spacer(Modifier.height(17.dp))
        Card(shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xAAFFFFFF))) {
            Text("🌼 Belajar itu menyenangkan bersama Neko! 🐱 🌼",
                Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3159A8), textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(54.dp))
    }
}

@Composable
private fun HomeTile(
    icon: String,
    title: String,
    topColor: Color,
    bottomColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(119.dp)
            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = bottomColor)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.95f), topColor, bottomColor)
        ))) {
            Box(Modifier.align(Alignment.TopCenter).fillMaxWidth(0.72f)
                .height(8.dp).padding(horizontal = 5.dp)
                .background(Color.White.copy(alpha = 0.65f),
                    RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)))
            Column(Modifier.fillMaxSize().padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Box(Modifier.size(53.dp).shadow(4.dp, CircleShape)
                    .background(Brush.verticalGradient(
                        listOf(Color.White, Color(0xFFEAF7FF))), CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 32.sp)
                }
                Spacer(Modifier.height(7.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Black,
                    color = Color(0xFF26334A), textAlign = TextAlign.Center,
                    lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun PageHeading(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, fontSize = 15.sp, color = Color.Gray)
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
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFBCE7FF)),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(item.letter, fontSize = 105.sp, fontWeight = FontWeight.Bold)
            Text("${item.letter} untuk ${item.word}", fontSize = 25.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(letterIllustration(item.word), fontSize = 92.sp,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            SpeechButton("Dengarkan") { speak("${item.letter}, untuk ${item.word}") }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { index = if (index == 0) 25 else index - 1 }) {
                    Text("Sebelumnya")
                }
                Button(onClick = { index = if (index == 25) 0 else index + 1 }) {
                    Text("Berikutnya")
                }
            }
            Text("${index + 1} dari 26 huruf", Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun HijaiyahScreen(speak: (String) -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val item = hijaiyahLetters[index]

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeading("Belajar Huruf Hijaiyah", "Kenali dan dengarkan 29 huruf hijaiyah")

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE4F5FF))
        ) {
            Column(
                Modifier.fillMaxWidth().padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Huruf ke-${index + 1}", fontSize = 16.sp,
                    color = Color(0xFF3159A8), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(item.letter, fontSize = 112.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF174A91))
                Text(item.name, fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFFE87525))
                Spacer(Modifier.height(12.dp))
                SpeechButton("Dengarkan") {
                    speak(item.name)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = index > 0,
                        onClick = { index-- }
                    ) {
                        Text("Sebelumnya")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = index < hijaiyahLetters.lastIndex,
                        onClick = { index++ }
                    ) {
                        Text("Berikutnya")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Pilih huruf", fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold, color = Color(0xFF174A91))
        Spacer(Modifier.height(10.dp))

        hijaiyahLetters.chunked(4).forEach { rowItems ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { letter ->
                    val letterIndex = hijaiyahLetters.indexOf(letter)
                    val selected = letterIndex == index
                    Card(
                        modifier = Modifier.weight(1f)
                            .padding(vertical = 4.dp)
                            .clickable { index = letterIndex },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFFFFD166)
                            else Color(0xFFFFFFFF)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(letter.letter, fontSize =  thirtySp,
                                color = Color(0xFF174A91), fontWeight = FontWeight.Bold)
                            Text(letter.name, fontSize = 10.sp,
                                color = Color(0xFF315078))
                        }
                    }
                }
                repeat(4 - rowItems.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}

private val thirtySp = 30.sp

@Composable
private fun NumberScreen(speak: (String) -> Unit) {
    var number by rememberSaveable { mutableIntStateOf(1) }
    val word = numberWords(number)
    Card(Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDDAA))) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(number.toString(), fontSize = 95.sp, fontWeight = FontWeight.Bold)
            Text(word, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text(numberIllustration(number), fontSize = 65.sp)
            Spacer(Modifier.height(12.dp))
            SpeechButton("Dengarkan") { speak(word) }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(enabled = number > 1, onClick = { number-- }) {
                    Text("Sebelumnya")
                }
                Button(enabled = number < 100, onClick = { number++ }) {
                    Text("Berikutnya")
                }
            }
            Text("Angka $number dari 100", Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun MathObjectGroup(count: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1D6))) {
        Column(Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$count apel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            (1..count).toList().chunked(5).forEach { group ->
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        PageHeading("Belajar Berhitung", "Ayo berlatih matematika!")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            MathObjectGroup(question.first, Modifier.weight(1f))
            Text(question.symbol, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            MathObjectGroup(question.second, Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        Text("${question.first} ${question.symbol} ${question.second} = ?",
            fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3159A8), textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        question.choices.forEach { choice ->
            Button(onClick = {
                if (choice == question.answer) {
                    message = "Hebat! Jawabanmu benar! ⭐"
                    onCorrect()
                    onSpeak("Hebat! Jawabanmu benar!")
                    question = newMathQuestion()
                } else {
                    message = "Belum tepat. Coba lagi ya!"
                    onSpeak("Coba lagi ya")
                }
            }, Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
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
    Card(Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3DDF0))) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(item.name, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(22.dp))
            Box(Modifier.size(190.dp).background(item.color, CircleShape),
                contentAlignment = Alignment.Center) {
                if (item.name == "Putih") {
                    Text("PUTIH", color = Color.Gray, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(22.dp))
            SpeechButton("Dengarkan") { speak(item.name) }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(enabled = index > 0, onClick = { index-- },
                    modifier = Modifier.weight(1f)) { Text("Sebelumnya") }
                Button(enabled = index < colors.lastIndex, onClick = { index++ },
                    modifier = Modifier.weight(1f)) { Text("Berikutnya") }
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        PageHeading("Belajar Hewan", "Kenali nama-nama hewan")
        animals.forEach { (emoji, name) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)
                .clickable { speak(name) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD8F1E0)),
                shape = RoundedCornerShape(16.dp)) {
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        PageHeading("Permainan Tebak Huruf", "Belajar bersama Neko!")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF246B45))) {
                Column(Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PAPAN PERMAINAN", color = Color(0xFFFFE082),
                        fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(12.dp))
                    Text("${question.word} dimulai dengan huruf apa?",
                        color = Color.White, fontSize = 19.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                        lineHeight = 25.sp)
                    Spacer(Modifier.height(14.dp))
                    choices.chunked(2).forEach { rowChoices ->
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowChoices.forEach { choice ->
                                Button(onClick = {
                                    if (choice.letter == question.letter) {
                                        message = "Benar! Kamu pintar! ⭐"
                                        onCorrect()
                                        onSpeak("Benar! ${question.word} dimulai dengan huruf ${question.letter}")
                                        question = letters.random()
                                    } else {
                                        message = "Belum benar. Coba lagi!"
                                        onSpeak("Coba lagi")
                                    }
                                }, modifier = Modifier.weight(1f).padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD166),
                                        contentColor = Color(0xFF26334A))) {
                                    Text(choice.letter, fontSize = 23.sp,
                                        fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(message, color = Color.White, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
            Column(Modifier.width(76.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text("🐱", fontSize = 55.sp)
                Text("Neko", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3159A8))
                Text("Ayo jawab!", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF3159A8), textAlign = TextAlign.Center)
                Text("👉", fontSize = 27.sp)
            }
        }
    }
}

@Composable
private fun AwardScreen(stars: Int) {
    Column(Modifier.fillMaxSize().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("🏆", fontSize = 90.sp)
        Text("Prestasi Belajar", fontSize = 29.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE18B)),
            shape = RoundedCornerShape(25.dp)) {
            Column(Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐", fontSize = 55.sp)
                Text("$stars Bintang", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(when {
                    stars >= 20 -> "Luar biasa! Kamu hebat!"
                    stars >= 10 -> "Hebat! Terus belajar!"
                    stars >= 5 -> "Bagus! Terus berlatih!"
                    else -> "Ayo bermain dan kumpulkan bintang!"
                }, fontSize = 17.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

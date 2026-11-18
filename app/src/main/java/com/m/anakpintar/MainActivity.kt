
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

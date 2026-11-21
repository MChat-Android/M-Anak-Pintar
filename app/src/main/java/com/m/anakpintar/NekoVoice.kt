
package com.m.anakpintar

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.io.File
import java.util.Locale
import java.security.MessageDigest

class NekoVoice(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var ready = false
    private var currentPlayer: MediaPlayer? = null
    private val pending = mutableListOf<String>()
    private var disposed = false

    private val folder = File(appContext.filesDir, "neko_audio")

    init {
        folder.mkdirs()

        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS && !disposed) {
                val engine = tts ?: return@TextToSpeech

                val result = engine.setLanguage(Locale("id", "ID"))

                if (result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    engine.setSpeechRate(0.88f)
                    engine.setPitch(1.22f)

                    engine.setOnUtteranceProgressListener(
                        object : UtteranceProgressListener() {
                            override fun onStart(id: String?) = Unit

                            override fun onDone(id: String?) {
                                if (id == null || disposed) return

                                val file = File(folder, "$id.wav")

                                if (file.exists()) {
                                    playFile(file)
                                }
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onError(id: String?) {
                                processNext()
                            }

                            override fun onError(id: String?, errorCode: Int) {
                                processNext()
                            }
                        }
                    )

                    ready = true
                    processNext()
                }
            }
        }
    }

    @Synchronized
    fun speak(text: String) {
        if (disposed || text.isBlank()) return

        val cleanText = text.trim()
        val file = File(folder, "${hash(cleanText)}.wav")

        if (file.exists() && file.length() > 100) {
            playFile(file)
            return
        }

        pending.add(cleanText)
        processNext()
    }

    @Synchronized
    private fun processNext() {
        if (!ready || disposed || pending.isEmpty()) return

        val text = pending.removeAt(0)
        val id = hash(text)
        val file = File(folder, "$id.wav")

        if (file.exists() && file.length() > 100) {
            playFile(file)
            return
        }

        val params = Bundle()

        tts?.synthesizeToFile(
            text,
            params,
            file,
            id
        )
    }

    @Synchronized
    private fun playFile(file: File) {
        if (disposed || !file.exists()) return

        try {
            currentPlayer?.release()
            currentPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)

                setOnCompletionListener { player ->
                    player.release()

                    if (currentPlayer === player) {
                        currentPlayer = null
                    }

                    processNext()
                }

                setOnErrorListener { player, _, _ ->
                    player.release()

                    if (currentPlayer === player) {
                        currentPlayer = null
                    }

                    processNext()
                    true
                }

                prepare()
                start()
            }
        } catch (_: Exception) {
            currentPlayer?.release()
            currentPlayer = null
            processNext()
        }
    }

    private fun hash(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun stop() {
        currentPlayer?.stopSafely()
        currentPlayer = null
        pending.clear()
        tts?.stop()
    }

    fun release() {
        disposed = true
        currentPlayer?.stopSafely()
        currentPlayer = null
        pending.clear()
        ready = false
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    private fun MediaPlayer.stopSafely() {
        try {
            if (isPlaying) stop()
        } catch (_: Exception) {
        }

        try {
            release()
        } catch (_: Exception) {
        }
    }
}

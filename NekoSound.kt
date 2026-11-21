
package com.m.anakpintar

import android.content.Context
import android.media.MediaPlayer

enum class NekoSoundType(val resourceId: Int) {
    HALO(R.raw.neko_halo),
    BENAR(R.raw.neko_benar),
    SALAH(R.raw.neko_salah),
    SEMANGAT(R.raw.neko_semangat),
    PINTAR(R.raw.neko_pintar),
    SELAMAT(R.raw.neko_selamat)
}

class NekoSound(context: Context) {

    private val appContext = context.applicationContext
    private var player: MediaPlayer? = null

    @Synchronized
    fun play(type: NekoSoundType) {
        stop()

        val newPlayer = MediaPlayer.create(
            appContext,
            type.resourceId
        ) ?: return

        player = newPlayer

        newPlayer.setOnCompletionListener { finished ->
            if (player === finished) player = null
            finished.release()
        }

        newPlayer.setOnErrorListener { failed, _, _ ->
            if (player === failed) player = null
            failed.release()
            true
        }

        newPlayer.start()
    }

    @Synchronized
    fun stop() {
        val oldPlayer = player
        player = null

        if (oldPlayer != null) {
            try {
                if (oldPlayer.isPlaying) oldPlayer.stop()
            } catch (_: Exception) {
            }

            try {
                oldPlayer.release()
            } catch (_: Exception) {
            }
        }
    }

    fun release() {
        stop()
    }
}

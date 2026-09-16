package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager {

    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    fun playPickup() {
        if (!isEnabled) return
        scope.launch {
            playTone(frequency = 520.0, durationMs = 60, startFreq = 400.0)
        }
    }

    fun playDrop() {
        if (!isEnabled) return
        scope.launch {
            playTone(frequency = 280.0, durationMs = 70, startFreq = 340.0)
        }
    }

    fun playInvalid() {
        if (!isEnabled) return
        scope.launch {
            playTone(frequency = 180.0, durationMs = 80)
        }
    }

    fun playLineClear(combo: Int) {
        if (!isEnabled) return
        scope.launch {
            // Pentatonic scale pitches that rise with combo streak
            val baseScale = listOf(523.25, 587.33, 659.25, 783.99, 880.00, 1046.50, 1174.66)
            val index = (combo.coerceAtLeast(1) - 1) % baseScale.size
            val f1 = baseScale[index]
            val f2 = f1 * 1.25 // Major 3rd
            val f3 = f1 * 1.5  // 5th

            // Fast arpeggio
            playTone(f1, durationMs = 70)
            playTone(f2, durationMs = 70)
            playTone(f3, durationMs = 120)
        }
    }

    fun playComboFanfare() {
        if (!isEnabled) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (note in notes) {
                playTone(note, durationMs = 80)
            }
        }
    }

    fun playGameOver() {
        if (!isEnabled) return
        scope.launch {
            val notes = listOf(440.0, 392.0, 349.23, 293.66)
            for (note in notes) {
                playTone(note, durationMs = 130)
            }
        }
    }

    private fun playTone(
        frequency: Double,
        durationMs: Int,
        startFreq: Double = frequency,
        volume: Float = 0.5f
    ) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (frequency - startFreq) * progress

                // Envelope: quick attack, smooth exponential decay
                val envelope = if (progress < 0.1) {
                    progress / 0.1
                } else {
                    exp(-4.0 * (progress - 0.1))
                }

                // Warm harmonic tone (fundamental + subtle 2nd harmonic)
                val sampleValue = (sin(2.0 * PI * currentFreq * t) * 0.8 +
                        sin(4.0 * PI * currentFreq * t) * 0.2) * envelope * volume

                buffer[i] = (sampleValue * Short.MAX_VALUE).toInt().coerceIn(
                    Short.MIN_VALUE.toInt(),
                    Short.MAX_VALUE.toInt()
                ).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            // Release after playing
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // AudioTrack failure fallback safely ignored
        }
    }
}

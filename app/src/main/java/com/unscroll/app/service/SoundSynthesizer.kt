package com.unscroll.app.service

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.sin
import kotlin.math.pow

object SoundSynthesizer {

    private var ambientTrack: AudioTrack? = null
    @Volatile
    private var isAmbientPlaying = false

    fun playSingingBowlChime(frequencyHz: Float = 432f, durationSeconds: Float = 2.5f) {
        thread(start = true, name = "ChimeThread") {
            try {
                val sampleRate = 44100
                val numSamples = (durationSeconds * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = Math.exp(-3.0 * (i.toDouble() / numSamples))
                    val fundamental = sin(2.0 * Math.PI * frequencyHz * time)
                    val harmonic = 0.5 * sin(2.0 * Math.PI * (frequencyHz * 1.5) * time)
                    val sample = ((fundamental + harmonic) * envelope * 0.4 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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

                track.write(buffer, 0, buffer.size)
                track.play()
                Thread.sleep((durationSeconds * 1000).toLong())
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    /** Ascending three-tone success fanfare */
    fun playSuccessChime() {
        thread(start = true, name = "SuccessThread") {
            try {
                val sampleRate = 44100
                val totalDuration = 0.9f
                val numSamples = (totalDuration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)
                val notes = floatArrayOf(523.25f, 659.25f, 783.99f) // C5, E5, G5

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val noteIndex = (i * 3) / numSamples
                    val freq = notes[noteIndex.coerceIn(0, 2)]
                    val notePos = (i.toDouble() - (noteIndex * numSamples / 3.0)) / (numSamples / 3.0)
                    val envelope = (1.0 - notePos).coerceIn(0.0, 1.0) * 0.6
                    val sample = (sin(2.0 * Math.PI * freq * time) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, totalDuration)
            } catch (_: Exception) {}
        }
    }

    /** Quick tactile tick sound for counters and sliders */
    fun playTick() {
        thread(start = true, name = "TickThread") {
            try {
                val sampleRate = 44100
                val duration = 0.04f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = Math.exp(-40.0 * time)
                    val sample = (sin(2.0 * Math.PI * 1200.0 * time) * envelope * 0.5 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Rising whoosh sound for transitions and swipes */
    fun playWhoosh() {
        thread(start = true, name = "WhooshThread") {
            try {
                val sampleRate = 44100
                val duration = 0.3f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = 200.0 + 800.0 * progress
                    val envelope = sin(Math.PI * progress) * 0.3
                    val noise = (Math.random() * 2.0 - 1.0) * 0.15 * envelope
                    val tone = sin(2.0 * Math.PI * freq * time) * envelope
                    val sample = ((tone + noise) * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Warning descending two-tone */
    fun playWarning() {
        thread(start = true, name = "WarningThread") {
            try {
                val sampleRate = 44100
                val duration = 0.4f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = if (progress < 0.5) 880.0 else 660.0
                    val envelope = (1.0 - progress) * 0.5
                    val sample = (sin(2.0 * Math.PI * freq * time) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Heartbeat-like pulse for urgency */
    fun playHeartbeat() {
        thread(start = true, name = "HeartbeatThread") {
            try {
                val sampleRate = 44100
                val duration = 0.6f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    // Two bumps like a heartbeat
                    val bump1 = if (progress < 0.15) sin(Math.PI * progress / 0.15) else 0.0
                    val bump2 = if (progress in 0.25..0.40) sin(Math.PI * (progress - 0.25) / 0.15) * 0.7 else 0.0
                    val envelope = (bump1 + bump2) * 0.4
                    val sample = (sin(2.0 * Math.PI * 60.0 * time) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Gentle level-up ascending arpeggio */
    fun playLevelUp() {
        thread(start = true, name = "LevelUpThread") {
            try {
                val sampleRate = 44100
                val duration = 1.2f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)
                val notes = floatArrayOf(392f, 494f, 587f, 784f) // G4, B4, D5, G5

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val noteIndex = (i * 4) / numSamples
                    val freq = notes[noteIndex.coerceIn(0, 3)]
                    val notePos = (i.toDouble() - (noteIndex * numSamples / 4.0)) / (numSamples / 4.0)
                    val envelope = Math.exp(-2.5 * notePos) * 0.45
                    val fundamental = sin(2.0 * Math.PI * freq * time)
                    val harmonic = 0.3 * sin(2.0 * Math.PI * freq * 2 * time)
                    val sample = ((fundamental + harmonic) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int, durationSeconds: Float) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
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

        track.write(buffer, 0, buffer.size)
        track.play()
        Thread.sleep((durationSeconds * 1000).toLong())
        track.release()
    }

    fun startAmbientRain() {
        if (isAmbientPlaying) return
        isAmbientPlaying = true

        thread(start = true, name = "AmbientRainThread") {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                ambientTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                ambientTrack?.play()

                val buffer = ShortArray(minBufferSize)
                var lastOut = 0.0

                while (isAmbientPlaying) {
                    for (i in buffer.indices) {
                        val white = (Math.random() * 2.0 - 1.0)
                        lastOut = (lastOut * 0.88 + white * 0.12)
                        val sample = (lastOut * 0.18 * Short.MAX_VALUE).toInt()
                        buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                    ambientTrack?.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {
            } finally {
                ambientTrack?.release()
                ambientTrack = null
            }
        }
    }

    fun stopAmbientRain() {
        isAmbientPlaying = false
    }

    fun isAmbientActive(): Boolean = isAmbientPlaying
}

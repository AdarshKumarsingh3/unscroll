package com.unscroll.app.service

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.sin
import kotlin.math.pow
import kotlin.math.cos

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
                    val shimmer = 0.15 * sin(2.0 * Math.PI * (frequencyHz * 2.0) * time) * Math.exp(-5.0 * (i.toDouble() / numSamples))
                    val sample = ((fundamental + harmonic + shimmer) * envelope * 0.4 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, durationSeconds)
            } catch (_: Exception) {
            }
        }
    }

    /** Ascending three-tone sparkle fanfare with harmonics */
    fun playSuccessChime() {
        thread(start = true, name = "SuccessThread") {
            try {
                val sampleRate = 44100
                val totalDuration = 1.0f
                val numSamples = (totalDuration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)
                val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.5f) // C5, E5, G5, C6

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val noteIndex = (i * 4) / numSamples
                    val freq = notes[noteIndex.coerceIn(0, 3)]
                    val notePos = (i.toDouble() - (noteIndex * numSamples / 4.0)) / (numSamples / 4.0)
                    val envelope = Math.exp(-3.0 * notePos) * 0.5
                    val fundamental = sin(2.0 * Math.PI * freq * time)
                    val octave = 0.25 * sin(2.0 * Math.PI * freq * 2 * time)
                    val sample = ((fundamental + octave) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, totalDuration)
            } catch (_: Exception) {}
        }
    }

    /** Quick soft pop sound — like tapping a bubble */
    fun playTick() {
        thread(start = true, name = "TickThread") {
            try {
                val sampleRate = 44100
                val duration = 0.05f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = Math.exp(-50.0 * time)
                    val pop = sin(2.0 * Math.PI * 900.0 * time) * 0.5 + sin(2.0 * Math.PI * 1800.0 * time) * 0.2
                    val sample = (pop * envelope * 0.4 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Airy whoosh sweep — rising then fading out */
    fun playWhoosh() {
        thread(start = true, name = "WhooshThread") {
            try {
                val sampleRate = 44100
                val duration = 0.35f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = 150.0 + 1200.0 * progress * progress
                    val envelope = sin(Math.PI * progress) * 0.25
                    val noise = (Math.random() * 2.0 - 1.0) * 0.2 * envelope
                    val tone = sin(2.0 * Math.PI * freq * time) * envelope * 0.6
                    val sample = ((tone + noise) * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Buzzy error / wrong answer — dissonant minor second */
    fun playWarning() {
        thread(start = true, name = "WarningThread") {
            try {
                val sampleRate = 44100
                val duration = 0.35f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val envelope = (1.0 - progress) * 0.4
                    // Dissonant minor second interval
                    val tone1 = sin(2.0 * Math.PI * 440.0 * time)
                    val tone2 = sin(2.0 * Math.PI * 466.16 * time) * 0.8 // A#4 clashes with A4
                    val sample = ((tone1 + tone2) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Heartbeat-like double thump */
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
                    val bump1 = if (progress < 0.15) sin(Math.PI * progress / 0.15) else 0.0
                    val bump2 = if (progress in 0.25..0.40) sin(Math.PI * (progress - 0.25) / 0.15) * 0.7 else 0.0
                    val envelope = (bump1 + bump2) * 0.45
                    val sample = (sin(2.0 * Math.PI * 55.0 * time) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Triumphant ascending arpeggio with rich harmonics */
    fun playLevelUp() {
        thread(start = true, name = "LevelUpThread") {
            try {
                val sampleRate = 44100
                val duration = 1.4f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)
                val notes = floatArrayOf(392f, 494f, 587f, 784f, 988f) // G4, B4, D5, G5, B5

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val noteIndex = (i * 5) / numSamples
                    val freq = notes[noteIndex.coerceIn(0, 4)]
                    val notePos = (i.toDouble() - (noteIndex * numSamples / 5.0)) / (numSamples / 5.0)
                    val envelope = Math.exp(-2.0 * notePos) * 0.4
                    val fundamental = sin(2.0 * Math.PI * freq * time)
                    val fifth = 0.2 * sin(2.0 * Math.PI * freq * 1.5 * time)
                    val octave = 0.15 * sin(2.0 * Math.PI * freq * 2 * time)
                    val sample = ((fundamental + fifth + octave) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Zen gong — deep resonant hit with slow decay */
    fun playZenGong() {
        thread(start = true, name = "ZenGongThread") {
            try {
                val sampleRate = 44100
                val duration = 3.0f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = Math.exp(-1.5 * (i.toDouble() / numSamples))
                    val fundamental = sin(2.0 * Math.PI * 110.0 * time) // A2 — deep
                    val partial2 = 0.6 * sin(2.0 * Math.PI * 277.18 * time) // inharmonic partial
                    val partial3 = 0.3 * sin(2.0 * Math.PI * 440.0 * time) // A4
                    val wobble = 1.0 + 0.003 * sin(2.0 * Math.PI * 5.5 * time) // slight vibrato
                    val sample = ((fundamental * wobble + partial2 + partial3) * envelope * 0.35 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Xylophone tap — bright crystalline hit */
    fun playXylophone(noteFreq: Float = 880f) {
        thread(start = true, name = "XyloThread") {
            try {
                val sampleRate = 44100
                val duration = 0.5f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = Math.exp(-8.0 * (i.toDouble() / numSamples))
                    val fundamental = sin(2.0 * Math.PI * noteFreq * time)
                    val harmonic3 = 0.4 * sin(2.0 * Math.PI * noteFreq * 3 * time) * Math.exp(-12.0 * (i.toDouble() / numSamples))
                    val harmonic5 = 0.15 * sin(2.0 * Math.PI * noteFreq * 5 * time) * Math.exp(-15.0 * (i.toDouble() / numSamples))
                    val sample = ((fundamental + harmonic3 + harmonic5) * envelope * 0.45 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Water drop — short descending chirp like a droplet */
    fun playWaterDrop() {
        thread(start = true, name = "WaterDropThread") {
            try {
                val sampleRate = 44100
                val duration = 0.2f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = 2000.0 * Math.exp(-8.0 * progress)
                    val envelope = Math.exp(-10.0 * progress) * 0.5
                    val sample = (sin(2.0 * Math.PI * freq * time) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Wind chime — two gentle overlapping high notes */
    fun playWindChime() {
        thread(start = true, name = "WindChimeThread") {
            try {
                val sampleRate = 44100
                val duration = 1.5f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val env1 = Math.exp(-2.5 * progress)
                    val env2 = if (progress > 0.15) Math.exp(-2.5 * (progress - 0.15)) * 0.7 else 0.0
                    val chime1 = sin(2.0 * Math.PI * 1318.5 * time) * env1 // E6
                    val chime2 = sin(2.0 * Math.PI * 1760.0 * time) * env2 // A6
                    val sample = ((chime1 + chime2) * 0.3 * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Power up — sci-fi rising sweep with thick harmonics */
    fun playPowerUp() {
        thread(start = true, name = "PowerUpThread") {
            try {
                val sampleRate = 44100
                val duration = 0.8f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = 200.0 + 600.0 * progress * progress
                    val envelope = sin(Math.PI * progress) * 0.4
                    val fundamental = sin(2.0 * Math.PI * freq * time)
                    val square = 0.3 * (if (sin(2.0 * Math.PI * freq * time) > 0) 1.0 else -1.0)
                    val sample = ((fundamental + square) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate, duration)
            } catch (_: Exception) {}
        }
    }

    /** Coin collect — classic bright ding */
    fun playCoinCollect() {
        thread(start = true, name = "CoinThread") {
            try {
                val sampleRate = 44100
                val duration = 0.3f
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val envelope = Math.exp(-6.0 * progress) * 0.5
                    // Two notes: E6 then B6
                    val freq = if (progress < 0.35) 1318.5 else 1975.5
                    val fundamental = sin(2.0 * Math.PI * freq * time)
                    val harm = 0.3 * sin(2.0 * Math.PI * freq * 2 * time) * Math.exp(-10.0 * progress)
                    val sample = ((fundamental + harm) * envelope * Short.MAX_VALUE).toInt()
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

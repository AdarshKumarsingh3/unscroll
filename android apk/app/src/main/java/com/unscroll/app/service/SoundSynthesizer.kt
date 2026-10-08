package com.unscroll.app.service

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.sin

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
                    // Harmonic bell decay envelope
                    val envelope = Math.exp(-3.0 * (i.toDouble() / numSamples))
                    // Fundamental tone + Fifth harmonic
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
                        // Soft pink/rain filter
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

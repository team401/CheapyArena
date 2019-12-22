package org.team401.fms.manager

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

object SoundManager {
    enum class Sounds {
        MATCH_START,
        AUTO_END,
        TELEOP_START,
        MATCH_END,
        MATCH_ABORTED
    }

    private class LoadedSound(val bytes: ByteArray, val format: AudioFormat)
    private val soundMap = hashMapOf<Sounds, LoadedSound>()

    private fun loadSound(name: String): LoadedSound {
        val stream = AudioSystem.getAudioInputStream(javaClass.classLoader.getResource("sound/$name"))
        return LoadedSound(stream.readAllBytes(), stream.format)
    }

    fun init() {
        soundMap[Sounds.MATCH_START] = loadSound("match_start.wav")
        soundMap[Sounds.AUTO_END] = loadSound("auto_end.wav")
        soundMap[Sounds.TELEOP_START] = loadSound("teleop_start.wav")
        soundMap[Sounds.MATCH_END] = loadSound("match_end.wav")
        soundMap[Sounds.MATCH_ABORTED] = loadSound("match_aborted.wav")
    }

    fun playSound(sound: Sounds) {
        val loaded = soundMap[sound] ?: return
        val clip = AudioSystem.getClip()
        clip.open(loaded.format, loaded.bytes, 0, loaded.bytes.size)
        clip.start()
    }
}
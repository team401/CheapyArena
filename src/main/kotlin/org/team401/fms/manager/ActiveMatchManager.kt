package org.team401.fms.manager

import javafx.application.Platform
import org.team401.fms.data.Match
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ActiveMatchManager {
    private val executor = Executors.newSingleThreadScheduledExecutor()

    enum class MatchPhases {
        NONE, //No match loaded
        LOADED, //Match has just been loaded into the active manager
        CONFIGURED, //Field has been configured for this match
        ARMED, //Field is armed
        STARTED, //Match has started
        AUTO, //Auto is in progress
        PAUSE, //Pause between auto and tele is in progress
        TELEOP, //Teleop is in progress
        ENDED //Match has ended
    }

    var activeMatch: Match? = null
        @Synchronized get
        private set

    var activePhase = MatchPhases.NONE
        @Synchronized get
        private set

    var timer = 0L
        @Synchronized get 
        private set
    
    var blue1Estop = false 
        @Synchronized get
        private set

    var blue2Estop = false
        @Synchronized get
        private set

    var blue3Estop = false
        @Synchronized get
        private set

    var red1Estop = false
        @Synchronized get
        private set

    var red2Estop = false
        @Synchronized get
        private set

    var red3Estop = false
        @Synchronized get
        private set

    private var startTime = 0L

    private fun resetEstops() {
        blue1Estop = false
        blue2Estop = false
        blue3Estop = false
        red1Estop = false
        red2Estop = false
        red3Estop = false
    }

    @Synchronized fun start() {
        executor.scheduleAtFixedRate(::updateTimer, 0L, 50L, TimeUnit.MILLISECONDS)
    }

    @Synchronized fun loadMatch(index: Int) {
        val match = MatchScheduleManager.matchAt(index)
        match.setPlayed(false) //Reset the played status of the match
        activeMatch = match
        activePhase = MatchPhases.LOADED //Mark the phase as loaded
        timer = 0L //Reset timer
        resetEstops()
    }

    @Synchronized fun isFieldConfigurable(): Boolean {
        return (activePhase != MatchPhases.STARTED
                && activePhase != MatchPhases.AUTO
                && activePhase != MatchPhases.PAUSE
                && activePhase != MatchPhases.TELEOP
                && activePhase != MatchPhases.ENDED)
    }

    @Synchronized fun isArmable(): Boolean {
        return activePhase == MatchPhases.CONFIGURED
    }

    @Synchronized fun isStartable(): Boolean {
        return activePhase == MatchPhases.ARMED
    }

    @Synchronized fun isAbortable(): Boolean {
        return (activePhase == MatchPhases.STARTED
                || activePhase == MatchPhases.AUTO
                || activePhase == MatchPhases.PAUSE
                || activePhase == MatchPhases.TELEOP)
    }
    
    @Synchronized fun isBlue1Estoppable(): Boolean {
        return (activeMatch != null && activeMatch?.getBlue1() != 0 && !blue1Estop && isAbortable())
    }

    @Synchronized fun isBlue2Estoppable(): Boolean {
        return (activeMatch != null && activeMatch?.getBlue2() != 0 && !blue2Estop && isAbortable())
    }

    @Synchronized fun isBlue3Estoppable(): Boolean {
        return (activeMatch != null && activeMatch?.getBlue3() != 0 && !blue3Estop && isAbortable())
    }

    @Synchronized fun isRed1Estoppable(): Boolean {
        return (activeMatch != null && activeMatch?.getRed1() != 0 && !red1Estop && isAbortable())
    }

    @Synchronized fun isRed2Estoppable(): Boolean {
        return (activeMatch != null && activeMatch?.getRed2() != 0 && !red2Estop && isAbortable())
    }

    @Synchronized fun isRed3Estoppable(): Boolean {
        return (activeMatch != null && activeMatch?.getRed3() != 0 && !red3Estop && isAbortable())
    }
    
    @Synchronized fun estopBlue1() {
        if (isBlue1Estoppable()) blue1Estop = true
    }

    @Synchronized fun estopBlue2() {
        if (isBlue2Estoppable()) blue2Estop = true
    }

    @Synchronized fun estopBlue3() {
        if (isBlue3Estoppable()) blue3Estop = true
    }

    @Synchronized fun estopRed1() {
        if (isRed1Estoppable()) red1Estop = true
    }

    @Synchronized fun estopRed2() {
        if (isRed2Estoppable()) red2Estop = true
    }

    @Synchronized fun estopRed3() {
        if (isRed3Estoppable()) red3Estop = true
    }

    @Synchronized fun isMatchRunning(): Boolean {
        return isAbortable()
    }

    @Synchronized fun isNewMatchLoadable(): Boolean {
        return (!isMatchRunning())
    }

    /**
     * Configures the field.  Calls the callback when the field is configured, passing the result of the configuration
     */
    @Synchronized fun configureField(callback: (Boolean) -> Unit) {
        if (!isFieldConfigurable()) {
            //Do not allow field configuration in these phases
            return
        }
        //TODO add field configure mode
        activePhase = MatchPhases.CONFIGURED //Mark the phase
        callback(true) //Call the callback
    }

    @Synchronized fun armOrStart() {
        if (isStartable()) startMatch()
        else armField()
    }

    @Synchronized fun armField() {
        if (isArmable()) {
            //Only allow arming if the field is in the configured state
            //TODO notify bypass here
            activePhase = MatchPhases.ARMED
            resetEstops() //Reset estops on arm
        }
    }
    
    @Synchronized fun startMatch() {
        if (isStartable()) {
            println("MATCH STARTED")
            activePhase = MatchPhases.STARTED //Start the match
            timer = if (SettingsManager.autoTime == 0L) {
                SettingsManager.teleopTime //Set the timer to the teleop time
            } else {
                SettingsManager.autoTime //Set the timer to the auto time
            }
        }
    }
    
    @Synchronized fun abortMatch() {
        if (isAbortable()) {
            //Only allow aborting in these states
            activePhase = MatchPhases.CONFIGURED //Jump back to configured
            timer = 0L
            SoundManager.playSound(SoundManager.Sounds.MATCH_ABORTED)
        }
    }

    @Synchronized fun getStatusString(): String {
        return when (activePhase) {
            MatchPhases.NONE -> "NO MATCH LOADED"
            MatchPhases.LOADED -> "READY TO CONFIGURE"
            MatchPhases.CONFIGURED -> "READY TO ARM"
            MatchPhases.ARMED -> "READY TO START"
            MatchPhases.STARTED -> "READY TO START" //So we don't flash different text for 1 frame
            MatchPhases.AUTO -> "AUTO ACTIVE"
            MatchPhases.PAUSE -> "TRANSITION PERIOD"
            MatchPhases.TELEOP -> "TELEOP ACTIVE"
            MatchPhases.ENDED -> "MATCH ENDED"
        }
    }

    @Synchronized fun updateTimer() {
        val tNow = System.currentTimeMillis()
        val elapsed = tNow - startTime

        when (activePhase) {
            MatchPhases.STARTED -> {
                startTime = tNow
                if (SettingsManager.autoTime == 0L) {
                    //There is no auto, go straight to teleop
                    activePhase = MatchPhases.TELEOP
                    timer = SettingsManager.teleopTime //Set the timer to the teleop time
                } else {
                    //There is an auto, go into it
                    activePhase = MatchPhases.AUTO
                    timer = SettingsManager.autoTime //Set the timer to the auto time
                }
                SoundManager.playSound(SoundManager.Sounds.MATCH_START)
            }

            MatchPhases.AUTO -> {
                //Set timer to original - elapsed
                timer = SettingsManager.autoTime - elapsed
                if (timer <= 0L) {
                    timer = 0L
                    //Advance to next match phase
                    activePhase = MatchPhases.PAUSE
                    SoundManager.playSound(SoundManager.Sounds.AUTO_END)
                }
            }

            MatchPhases.PAUSE -> {
                timer = SettingsManager.autoTime + SettingsManager.pauseTime - elapsed
                if (timer <= 0L) {
                    timer = 0L
                    //Advance
                    activePhase = MatchPhases.TELEOP
                    SoundManager.playSound(SoundManager.Sounds.TELEOP_START)
                }
            }

            MatchPhases.TELEOP -> {
                timer = SettingsManager.autoTime + SettingsManager.pauseTime + SettingsManager.teleopTime - elapsed
                if (timer < 0L) {
                    timer = 0L
                    //End match
                    activePhase = MatchPhases.ENDED
                    SoundManager.playSound(SoundManager.Sounds.MATCH_END)
                    Platform.runLater { //This needs to run in the JavaFX thread since it updates UI elements
                        activeMatch?.setPlayed(true) //Mark the match as played
                    }
                }
            }

            else -> {} //Do nothing in any other phase
        }
    }
    
    @Synchronized fun getBlue1String(): String {
        if (activeMatch == null) return "----"
        val team = activeMatch!!.getBlue1()
        if (team == 0) return "----"
        if (team < 10) return "   $team"
        if (team < 100) return "  $team"
        if (team < 1000) return " $team"
        return team.toString()
    }

    @Synchronized fun getBlue2String(): String {
        if (activeMatch == null) return "----"
        val team = activeMatch!!.getBlue2()
        if (team == 0) return "----"
        if (team < 10) return "   $team"
        if (team < 100) return "  $team"
        if (team < 1000) return " $team"
        return team.toString()
    }

    @Synchronized fun getBlue3String(): String {
        if (activeMatch == null) return "----"
        val team = activeMatch!!.getBlue3()
        if (team == 0) return "----"
        if (team < 10) return "   $team"
        if (team < 100) return "  $team"
        if (team < 1000) return " $team"
        return team.toString()
    }

    @Synchronized fun getRed1String(): String {
        if (activeMatch == null) return "----"
        val team = activeMatch!!.getRed1()
        if (team == 0) return "----"
        if (team < 10) return "   $team"
        if (team < 100) return "  $team"
        if (team < 1000) return " $team"
        return team.toString()
    }

    @Synchronized fun getRed2String(): String {
        if (activeMatch == null) return "----"
        val team = activeMatch!!.getRed2()
        if (team == 0) return "----"
        if (team < 10) return "   $team"
        if (team < 100) return "  $team"
        if (team < 1000) return " $team"
        return team.toString()
    }

    @Synchronized fun getRed3String(): String {
        if (activeMatch == null) return "----"
        val team = activeMatch!!.getRed3()
        if (team == 0) return "----"
        if (team < 10) return "   $team"
        if (team < 100) return "  $team"
        if (team < 1000) return " $team"
        return team.toString()
    }

    private fun msToMinutes(ms: Long): Long {
        return (ms / (1000L * 60L)) % 60L
    }

    private fun msToSeconds(ms: Long): Long {
        return (ms / 1000L) % 60L
    }

    private fun msToMs(ms: Long): Long {
        return ms % 1000L / 100L
    }

    private fun msToTimeString(ms: Long): String {
        return "${msToMinutes(ms)}:${String.format("%02d", msToSeconds(ms))}.${msToMs(ms)}"
    }

    @Synchronized fun getTimerString(): String {
        return when (activePhase) {
            MatchPhases.ARMED -> {
                if (SettingsManager.autoTime == 0L) {
                    //There is no auto, return the teleop time
                    msToTimeString(SettingsManager.teleopTime)
                } else {
                    msToTimeString(SettingsManager.autoTime)
                }
            }
            MatchPhases.STARTED, MatchPhases.AUTO, MatchPhases.PAUSE, MatchPhases.TELEOP -> {
                msToTimeString(timer)
            }
            else -> "" //No timer otherwise
        }
    }
}
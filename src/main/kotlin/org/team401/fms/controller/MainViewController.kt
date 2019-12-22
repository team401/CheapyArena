package org.team401.fms.controller

import javafx.animation.AnimationTimer
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.paint.Color
import javafx.util.Callback
import org.team401.fms.FMS
import org.team401.fms.data.Match
import org.team401.fms.manager.ActiveMatchManager
import org.team401.fms.manager.MatchScheduleManager

class MainViewController {
    @FXML lateinit var editSchedule: Button
    @FXML lateinit var settings: Button
    @FXML lateinit var schedule: ListView<Match>
    @FXML lateinit var schedulePrev: Button
    @FXML lateinit var scheduleNext: Button
    @FXML lateinit var scheduleLoad: Button
    @FXML lateinit var blue1: Label
    @FXML lateinit var blue2: Label
    @FXML lateinit var blue3: Label
    @FXML lateinit var red1: Label
    @FXML lateinit var red2: Label
    @FXML lateinit var red3: Label
    @FXML lateinit var estopBlue1: Button
    @FXML lateinit var estopBlue2: Button
    @FXML lateinit var estopBlue3: Button
    @FXML lateinit var estopRed1: Button
    @FXML lateinit var estopRed2: Button
    @FXML lateinit var estopRed3: Button
    @FXML lateinit var status: Label
    @FXML lateinit var timer: Label
    @FXML lateinit var configField: Button
    @FXML lateinit var armStart: Button
    @FXML lateinit var abort: Button

    private val animationTimer = object : AnimationTimer() {
        override fun handle(now: Long) {
            updateMatchPlay() //Periodically update match play
        }
    }

    fun initialize() {
        schedule.cellFactory = Callback {
            object : ListCell<Match>() {
                override fun updateItem(item: Match?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null) {
                        text = null
                    } else {
                        //Draw match
                        val teamStr = item.getBluePlayingTeamsAsList().joinToString() + " vs. " + item.getRedPlayingTeamsAsList().joinToString()
                        textFill = if (isSelected) {
                            Color.WHITE
                        } else {
                            if (item.getPlayed()) {
                                Color.GREEN
                            } else {
                                Color.BLACK
                            }
                        }
                        text = "Match ${index + 1} - $teamStr${if (item.getPlayed()) " - PLAYED" else ""}"
                    }
                }
            }
        }

        schedule.selectionModel.selectedIndexProperty().addListener(::onScheduleSelect)

        schedulePrev.isDisable = true
        scheduleNext.isDisable = true
        scheduleLoad.isDisable = true

        schedule.items = MatchScheduleManager.matches

        MatchScheduleManager.matches.add(Match(401, 346, 0, 1086, 0, 0))
        MatchScheduleManager.matches[0].setPlayed(true)
        MatchScheduleManager.matches.add(Match(4201, 346, 0, 1086, 0, 0))

        animationTimer.start()

    }

    private fun onScheduleSelect(observable: ObservableValue<out Number>?, oldValue: Number, newValue: Number) {
        val value = newValue.toInt()
        if (value >= 0) {
            //An item is selected, enable the buttons
            schedulePrev.isDisable = value == 0 //Disable previous if element is the first in the list
            scheduleNext.isDisable = value == MatchScheduleManager.size() - 1 //Disable if element is last in the list
        } else {
            schedulePrev.isDisable = true //Disable all buttons
            scheduleNext.isDisable = true
        }
    }

    @FXML fun onSchedulePrev(e: ActionEvent) {
        //Select the previous item in the list
        val newSelection = schedule.selectionModel.selectedIndex - 1
        if (newSelection < 0) return
        schedule.selectionModel.select(newSelection)
    }

    @FXML fun onScheduleNext(e: ActionEvent) {
        //Select the next item in the list
        val newSelection = schedule.selectionModel.selectedIndex + 1
        if (newSelection >= MatchScheduleManager.size()) return
        schedule.selectionModel.select(newSelection)
    }

    @FXML fun onScheduleLoad(e: ActionEvent) {
        //Get selected match
        val selectedMatch = schedule.selectionModel.selectedIndex
        //Load the match into the active match manager
        ActiveMatchManager.loadMatch(selectedMatch)
    }

    @FXML fun onConfigure(e: ActionEvent) {
        ActiveMatchManager.configureField {
            println("done")
        }
    }

    @FXML fun onArmStart(e: ActionEvent) {
        ActiveMatchManager.armOrStart()
    }

    @FXML fun onAbort(e: ActionEvent) {
        ActiveMatchManager.abortMatch()
    }

    @FXML fun onEditSchedule(e: ActionEvent) {
        FMS.createAndShowScheduleEditor()
    }
    
    @FXML fun onBlue1Estop(e: ActionEvent) {
        ActiveMatchManager.estopBlue1()
    }

    @FXML fun onBlue2Estop(e: ActionEvent) {
        ActiveMatchManager.estopBlue2()
    }

    @FXML fun onBlue3Estop(e: ActionEvent) {
        ActiveMatchManager.estopBlue3()
    }

    @FXML fun onRed1Estop(e: ActionEvent) {
        ActiveMatchManager.estopRed1()
    }

    @FXML fun onRed2Estop(e: ActionEvent) {
        ActiveMatchManager.estopRed2()
    }

    @FXML fun onRed3Estop(e: ActionEvent) {
        ActiveMatchManager.estopRed3()
    }

    /**
     * Updates the match play section.  This is called periodically
     */
    private fun updateMatchPlay() {
        //Get new strings from match manager
        val statusStr = ActiveMatchManager.getStatusString()
        val timerStr = ActiveMatchManager.getTimerString()
        val blue1Str = ActiveMatchManager.getBlue1String()
        val blue2Str = ActiveMatchManager.getBlue2String()
        val blue3Str = ActiveMatchManager.getBlue3String()
        val red1Str = ActiveMatchManager.getRed1String()
        val red2Str = ActiveMatchManager.getRed2String()
        val red3Str = ActiveMatchManager.getRed3String()

        val configurable = ActiveMatchManager.isFieldConfigurable()

        val startable = ActiveMatchManager.isStartable()
        val armable = ActiveMatchManager.isArmable()
        val loadable = ActiveMatchManager.isNewMatchLoadable()
        val abortable = ActiveMatchManager.isAbortable()
        
        val blue1Estoppable = ActiveMatchManager.isBlue1Estoppable()
        val blue2Estoppable = ActiveMatchManager.isBlue2Estoppable()
        val blue3Estoppable = ActiveMatchManager.isBlue3Estoppable()
        val red1Estoppable = ActiveMatchManager.isRed1Estoppable()
        val red2Estoppable = ActiveMatchManager.isRed2Estoppable()
        val red3Estoppable = ActiveMatchManager.isRed3Estoppable()

        status.text = statusStr
        timer.text = timerStr
        blue1.text = blue1Str
        blue2.text = blue2Str
        blue3.text = blue3Str
        red1.text = red1Str
        red2.text = red2Str
        red3.text = red3Str

        configField.isDisable = !configurable
        if (!armable && !startable) {
            armStart.isDisable = true
            armStart.text = "Arm Match"
            armStart.style = ""
        } else if (startable) {
            armStart.isDisable = false
            armStart.text = "Start Match"
            armStart.style = "-fx-base: cyan" //Make the button very visible
        } else {
            armStart.isDisable = false
            armStart.text = "Arm Match"
            armStart.style = ""
        }

        scheduleLoad.isDisable = !loadable || schedule.selectionModel.selectedIndex < 0

        abort.isDisable = !abortable
        estopBlue1.isDisable = !blue1Estoppable
        estopBlue2.isDisable = !blue2Estoppable
        estopBlue3.isDisable = !blue3Estoppable
        estopRed1.isDisable = !red1Estoppable
        estopRed2.isDisable = !red2Estoppable
        estopRed3.isDisable = !red3Estoppable
    }
}
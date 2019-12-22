package org.team401.fms.manager

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.util.Callback
import org.team401.fms.data.Match
import org.team401.fms.data.TeamAtEvent

/**
 * Manages the match schedule.  Updates to this should be done from the JavaFX thread
 */
object MatchScheduleManager {
    val matches = FXCollections.observableArrayList<Match> {
        arrayOf(
            it.blue1Property(),
            it.blue2Property(),
            it.blue3Property(),
            it.red1Property(),
            it.red2Property(),
            it.red3Property(),
            it.playedProperty()
        )
    }

    val teams = FXCollections.observableArrayList<TeamAtEvent> {
        arrayOf(
            it.numberProperty()
        )
    }

    /**
     * Add a blank match
     */
    fun addBlank() {
        matches.add(Match(0, 0, 0, 0, 0, 0))
    }

    fun deleteAt(row: Int) {
        matches.removeAt(row)
    }

    fun matchAt(index: Int) = matches[index]
    fun size() = matches.size

    fun cloneSchedule(): ObservableList<Match> {
        val list = FXCollections.observableArrayList<Match> {
            arrayOf(
                it.blue1Property(),
                it.blue2Property(),
                it.blue3Property(),
                it.red1Property(),
                it.red2Property(),
                it.red3Property(),
                it.playedProperty()
            )
        }

        matches.forEach {
            list.add(it.clone())
        }

        return list
    }

    fun setSchedule(list: ObservableList<Match>) {
        matches.setAll(list)
    }
}
package org.team401.fms.controller

import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.converter.IntegerStringConverter
import org.team401.fms.data.Match
import org.team401.fms.manager.MatchScheduleManager

class ScheduleViewController {
    @FXML lateinit var schedule: TableView<Match>

    @FXML lateinit var save: Button
    @FXML lateinit var cancel: Button

    lateinit var matchScheduleCopy: ObservableList<Match>

    fun initialize() {
        matchScheduleCopy = MatchScheduleManager.cloneSchedule()
        schedule.items = matchScheduleCopy

        //Configures the first column to display the match number (index + 1)
        schedule.columns[0].cellFactory = Callback {
            object : TableCell<Match, Void>() {
                override fun updateIndex(i: Int) {
                    super.updateIndex(i)
                    text = if (isEmpty) {
                        null
                    } else {
                        (i + 1).toString()
                    }
                }
            }
        }

        //Set value factories for each column (binds a property to the column)
        (schedule.columns[1] as TableColumn<Match, Int>).cellValueFactory = PropertyValueFactory<Match, Int>("blue1")
        (schedule.columns[2] as TableColumn<Match, Int>).cellValueFactory = PropertyValueFactory<Match, Int>("blue2")
        (schedule.columns[3] as TableColumn<Match, Int>).cellValueFactory = PropertyValueFactory<Match, Int>("blue3")
        (schedule.columns[4] as TableColumn<Match, Int>).cellValueFactory = PropertyValueFactory<Match, Int>("red1")
        (schedule.columns[5] as TableColumn<Match, Int>).cellValueFactory = PropertyValueFactory<Match, Int>("red2")
        (schedule.columns[6] as TableColumn<Match, Int>).cellValueFactory = PropertyValueFactory<Match, Int>("red3")
        (schedule.columns[7] as TableColumn<Match, Boolean>).cellValueFactory = PropertyValueFactory<Match, Boolean>("played")

        //Set value update listeners (renders cells within a column with the value)
        (schedule.columns[1] as TableColumn<Match, Int>).cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        (schedule.columns[2] as TableColumn<Match, Int>).cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        (schedule.columns[3] as TableColumn<Match, Int>).cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        (schedule.columns[4] as TableColumn<Match, Int>).cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        (schedule.columns[5] as TableColumn<Match, Int>).cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        (schedule.columns[6] as TableColumn<Match, Int>).cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        (schedule.columns[7] as TableColumn<Match, Boolean>).cellFactory = CheckBoxTableCell.forTableColumn(schedule.columns[7] as TableColumn<Match, Boolean>)
    }

    @FXML fun onAddMatch(event: ActionEvent) {
        matchScheduleCopy.add(Match(0, 0, 0, 0, 0, 0))
    }

    @FXML fun onDeleteMatch(event: ActionEvent) {
        val row = schedule.selectionModel.selectedIndex
        if (row >= 0) {
            matchScheduleCopy.removeAt(row) //Only delete if there is a row selected (row == -1 if no selection)
        }
    }

    @FXML fun onSave(e: ActionEvent) {
        val stage = save.scene.window as Stage
        //Merge the schedule changes in
        MatchScheduleManager.setSchedule(matchScheduleCopy)
        stage.close() //Close the window
    }

    @FXML fun onCancel(e: ActionEvent) {
        val stage = cancel.scene.window as Stage
        stage.close() //Close the window
    }
}
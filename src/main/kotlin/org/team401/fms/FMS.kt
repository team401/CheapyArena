package org.team401.fms

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import org.team401.fms.manager.ActiveMatchManager
import org.team401.fms.manager.SoundManager

class FMS: Application() {
    companion object {
        fun createAndShowScheduleEditor() {
            val location = FMS::class.java.classLoader.getResource("schedule_view.fxml")
            val loader = FXMLLoader(location)

            val root = loader.load<Pane>()

            val stage = Stage()

            stage.title = "Match Schedule"
            stage.initModality(Modality.APPLICATION_MODAL)

            stage.scene = Scene(root)
            stage.show()
        }
    }

    override fun start(stage: Stage) {
        val location = javaClass.classLoader.getResource("main_view.fxml")
        val loader = FXMLLoader(location)

        val root = loader.load<Pane>()

        stage.minWidth = 640.0
        stage.minHeight = 480.0
        stage.title = "Cheapy Arena"

        val scene = Scene(root)

        SoundManager.init()
        ActiveMatchManager.start()

        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(FMS::class.java)
}
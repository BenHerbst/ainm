package org.idaesbasic

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import org.fxmisc.richtext.CodeArea
import org.idaesbasic.buffer.NewBufferView
import org.idaesbasic.buffer.file.FileModel
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class MainView : View() {
    val controller: MainController by inject()

    override val root = borderpane {
        top {
            toolbar {
                // Left side
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-caret-left"
                        iconColor = Color.web("#f8f8f2")
                    }
                    action {
                        if (controller.currentBufferIndex > 0) {
                            controller.currentBufferIndex -= 1
                            controller.openCurrentBufferIndexBuffer()
                        }
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-caret-right"
                        iconColor = Color.web("#f8f8f2")
                    }
                    action {
                        if (controller.currentBufferIndex +1 < controller.buffers.size) {
                            controller.currentBufferIndex += 1
                            controller.openCurrentBufferIndexBuffer()
                        }
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        newBuffer()
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-plus"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        controller.removeBuffer(controller.currentBufferIndex)
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-minus"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        val currentEditor: Editor = controller.getCurrentBuffer() as Editor
                        showSaveDialogAndSaveText(
                            arrayOf(
                                FileChooser.ExtensionFilter("All", "*"),
                                FileChooser.ExtensionFilter("Plain text", "*.txt"),
                                FileChooser.ExtensionFilter("Java class", "*.java"),
                                FileChooser.ExtensionFilter("Python", "*.py"),
                                FileChooser.ExtensionFilter("Kotlin class", "*.kt"),
                                ),
                            currentEditor.root.text,
                            currentEditor.fileObject
                        )
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-save"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                // Space
                pane {
                    hboxConstraints {
                        hgrow = Priority.SOMETIMES
                    }
                }
                // Center
                textfield {
                    prefWidth = 600.0
                    prefHeight = 30.0
                }
                // Space
                pane {
                    hboxConstraints {
                        hgrow = Priority.SOMETIMES
                    }
                }
                // Right side
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                }
            }
        }
    }

    private fun showSaveDialogAndSaveText(extensions: Array<FileChooser.ExtensionFilter>, text: String, file: FileModel) {
        if (file.directory == null) {
            val fileArray = chooseFile(
                "Save file",
                extensions,
                null,
                null,
                FileChooserMode.Save
            )
            if (fileArray.isNotEmpty()) {
                val newDirectory = fileArray[0]
                file.directory = Paths.get(newDirectory.path)
            }
        }
        if (file.directory != null) {
            controller.saveTextToFile(text, file.directory!!)
        }
    }

    fun newEditor(bufferIndex: Int, file: FileModel) {
        val textEditor = Editor(file)
        controller.buffers[bufferIndex] = textEditor
    }

    fun newBuffer() {
        val textEditor = NewBufferView()
        controller.buffers = controller.buffers.plus(textEditor)
        controller.currentBufferIndex = controller.buffers.size -1
        controller.openCurrentBufferIndexBuffer()
    }

    fun switchCenterToBufferView(bufferView: Fragment) {
        root.center = bufferView.root
    }
}

class Editor(file: FileModel): Fragment() {
    override val root = CodeArea()
    lateinit var fileObject: FileModel

    init {
        fileObject = file
        root.padding = Insets(20.0, 20.0, 20.0, 20.0)
        root.appendText(file.text)
    }
}

class MainController : Controller() {

    var buffers = emptyArray<Fragment>()
    var currentBufferIndex = -1

    fun getCurrentBuffer(): Fragment {
        return buffers[currentBufferIndex]
    }
    fun openCurrentBufferIndexBuffer() {
        find(MainView::class).switchCenterToBufferView(getCurrentBuffer())
    }

    fun removeBuffer(index: Int) {
        val mutableBuffers = buffers.toMutableList()
        mutableBuffers.removeAt(index)
        buffers = mutableBuffers.toTypedArray()
        if (currentBufferIndex >= index && currentBufferIndex != 0) {
            currentBufferIndex -= 1
        }
        openCurrentBufferIndexBuffer()
    }

    fun saveTextToFile(text: String, file: Path) {
        Files.writeString(file, text)
    }

}
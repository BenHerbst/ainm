package org.idaesbasic

import tornadofx.*

class MainStyle: Stylesheet() {

   companion object {
       val styledTextArea by cssclass()
       val caret by cssclass()

       private val draculaBackgroundColor = c("#282a36")
       private val draculaForegroundColor = c("#44475a")
       private val draculaSelectColor = c("#6272a4")
       private val draculaTextColor = c("#f8f8f2")
   }

    init {
        button {
            backgroundColor = multi(draculaForegroundColor)
            and (hover) {
                backgroundColor = multi(draculaSelectColor)
            }
        }
        toolBar {
            backgroundColor = multi(draculaBackgroundColor)
        }
        textField {
            backgroundColor = multi(draculaForegroundColor)
            textFill = draculaTextColor
        }
        styledTextArea {
            backgroundColor = multi(draculaBackgroundColor)
            textFill = draculaTextColor
            fontSize = 14.px
            text {
                fill = draculaTextColor
            }
        }
        caret {
            stroke = draculaTextColor
        }
    }
}
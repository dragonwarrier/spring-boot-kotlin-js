package com.techprd.views

import com.techprd.model.Task
import com.techprd.services.StorageService
import com.techprd.utils.Utils.randomId
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.get
import kotlinx.browser.document

/**
 * generates a list of todo items
 */
fun DIV.listView(storage: StorageService, block: UL.() -> Unit) {
    ul {
        classes = setOf("collection with-header")
        li("collection-header cyan") {
            h5 { +"List of Tasks" }
        }
        storage.forEach { (_, task) ->
            li("collection-item avatar dismissable") {
                todoItem(storage, task) {}
            }
        }
        block()
    }
}

fun LI.todoItem(storage: StorageService, task: Task, block: DIV.() -> Unit) {
    val inputId = randomId()
    div {
        id = task.id
        i("material-icons circle green") {
            +"insert_chart"
        }
        span("title left-align") {
            if (task.isDone) {
                style = " text-decoration: line-through"
            }
            +task.text
        }
        p {
            +"Personal"
        }
        div("secondary-content") {
            input {
                id = inputId
                classes = setOf("filled-in")
                type = InputType.checkBox
                checked = task.isDone
            }
            label {
                htmlFor = inputId
                +"Done"
                onClickFunction = markAsDone(storage, task)
            }
        }
        block()
    }
}

fun markAsDone(storage: StorageService, task: Task): (Event) -> Unit {
    return {
        val todoItem = document.getElementById(task.id) as HTMLDivElement
        val checkbox = todoItem.getElementsByTagName("input")[0] as HTMLInputElement
        val title = todoItem.getElementsByClassName("title")[0] as HTMLSpanElement
        if (!checkbox.checked) {
            task.isDone = true
            title.style.textDecoration = "line-through"
            storage.eventEmitter.trigger(storage.doneEvent, task)
        } else {
            task.isDone = false
            title.style.textDecoration = "none"
            storage.eventEmitter.trigger(storage.undoneEvent, task)
        }
    }
}

/**
 * generate input box for todo tasks
 */
@Suppress("UNCHECKED_CAST")
fun DIV.inputView(block: INPUT.() -> Unit) {
    input {
        id = "todo-input"
        classes = setOf("validate")
        type = InputType.text
        name = "task_desc"
        autoFocus = true
        block()
        onKeyPressFunction = onInputViewKeyPress() as (Event) -> Unit
    }
}

/**
 * Event that clear the value of input box on enter
 */
fun onInputViewKeyPress(): (KeyboardEvent) -> Unit {
    return {
        if (it.which == 13) {
            val input = it.currentTarget as HTMLInputElement
            input.value = ""
        }
    }
}

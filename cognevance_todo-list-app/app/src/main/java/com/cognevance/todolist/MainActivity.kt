package com.cognevance.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognevance.todolist.data.TaskRepository
import com.cognevance.todolist.data.TodoDatabase
import com.cognevance.todolist.ui.TaskViewModel
import com.cognevance.todolist.ui.TodoApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TodoDatabase.getInstance(applicationContext)
        val repository = TaskRepository(database.taskDao())

        setContent {
            val viewModel: TaskViewModel = viewModel(
                factory = TaskViewModel.Factory(repository)
            )
            TodoApp(viewModel)
        }
    }
}

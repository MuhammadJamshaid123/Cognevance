package com.cognevance.todolist.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val EDIT = "edit/{taskId}"

    fun editRoute(taskId: Long) = "edit/$taskId"
}

@Composable
fun TodoNavHost(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val editingId by viewModel.editingTaskId.collectAsState()

    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            TaskListScreen(
                viewModel = viewModel,
                onAddTask = {
                    viewModel.clearForm()
                    navController.navigate(Routes.ADD)
                },
                onEditTask = { task ->
                    viewModel.loadTaskForEdit(task)
                    navController.navigate(Routes.editRoute(task.id))
                }
            )
        }
        composable(Routes.ADD) {
            TaskFormScreen(
                viewModel = viewModel,
                isEditing = false,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) {
            TaskFormScreen(
                viewModel = viewModel,
                isEditing = editingId != null,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun TodoApp(viewModel: TaskViewModel) {
    MaterialTheme(colorScheme = androidx.compose.material3.lightColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            TodoNavHost(viewModel)
        }
    }
}

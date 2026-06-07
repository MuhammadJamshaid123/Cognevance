package com.cognevance.todolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognevance.todolist.data.TaskEntity
import com.cognevance.todolist.data.TaskPriority
import com.cognevance.todolist.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val titleError: String? = null
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    private val _editingTaskId = MutableStateFlow<Long?>(null)
    val editingTaskId: StateFlow<Long?> = _editingTaskId.asStateFlow()

    fun updateTitle(title: String) {
        _formState.value = _formState.value.copy(title = title, titleError = null)
    }

    fun updateDescription(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun updatePriority(priority: TaskPriority) {
        _formState.value = _formState.value.copy(priority = priority)
    }

    fun loadTaskForEdit(task: TaskEntity) {
        _editingTaskId.value = task.id
        _formState.value = TaskFormState(
            title = task.title,
            description = task.description,
            priority = task.priority
        )
    }

    fun clearForm() {
        _editingTaskId.value = null
        _formState.value = TaskFormState()
    }

    fun saveTask(onSuccess: () -> Unit) {
        val state = _formState.value
        if (state.title.isBlank()) {
            _formState.value = state.copy(titleError = "Title is required")
            return
        }
        if (state.title.length < 3) {
            _formState.value = state.copy(titleError = "Title must be at least 3 characters")
            return
        }

        viewModelScope.launch {
            val editId = _editingTaskId.value
            if (editId != null) {
                repository.updateTask(
                    TaskEntity(
                        id = editId,
                        title = state.title.trim(),
                        description = state.description.trim(),
                        priority = state.priority
                    )
                )
            } else {
                repository.addTask(
                    title = state.title.trim(),
                    description = state.description.trim(),
                    priority = state.priority
                )
            }
            clearForm()
            onSuccess()
        }
    }

    fun toggleComplete(task: TaskEntity) {
        viewModelScope.launch { repository.toggleComplete(task) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun clearCompleted() {
        viewModelScope.launch { repository.clearCompleted() }
    }

    class Factory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(repository) as T
        }
    }
}

package com.cognevance.todolist.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    suspend fun addTask(title: String, description: String, priority: TaskPriority): Long {
        return taskDao.insertTask(
            TaskEntity(title = title, description = description, priority = priority)
        )
    }

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun toggleComplete(task: TaskEntity) {
        taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun clearCompleted() = taskDao.deleteCompletedTasks()
}

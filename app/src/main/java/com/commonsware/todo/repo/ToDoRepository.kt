package com.commonsware.todo.repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

enum class FilterMode { ALL, OUTSTANDING, COMPLETED }
class ToDoRepository(
  private val store: ToDoEntity.Store,
  private val appScope: CoroutineScope
) {

  private fun filteredEntities(filterMode: FilterMode) = when (filterMode) {
    FilterMode.ALL -> store.all()
    FilterMode.OUTSTANDING -> store.filtered(isCompleted = false)
    FilterMode.COMPLETED -> store.filtered(isCompleted = true)
  }
  fun items(filterMode: FilterMode = FilterMode.ALL): Flow<List<ToDoModel>> =
    filteredEntities(filterMode).map { all -> all.map { it.toModel() } }
  fun find(id: String?): Flow<ToDoModel?> = store.find(id).map { it?.toModel() }
  suspend fun save(model: ToDoModel) {
    withContext(appScope.coroutineContext) {
      store.save(ToDoEntity(model))
    }
  }
  suspend fun delete(model: ToDoModel) {
    withContext(appScope.coroutineContext) {
      store.delete(ToDoEntity(model))
    }
  }
}
/*
import kotlinx.coroutines.CoroutineScope

class ToDoRepository (private val store: ToDoEntity.Store,
                      private val appScope: CoroutineScope){
  var items = emptyList<ToDoModel>()

  fun save(model: ToDoModel) {
    items = if (items.any { it.id == model.id }) {
      items.map { if (it.id == model.id) model else it }
    } else {
      items + model
    }
  }

  fun find(modelId: String?) = items.find { it.id == modelId }

  fun delete(model: ToDoModel) {
    items = items.filter { it.id != model.id }
  }
}*/

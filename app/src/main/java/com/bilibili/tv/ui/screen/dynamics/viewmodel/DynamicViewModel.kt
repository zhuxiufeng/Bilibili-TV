package com.bilibili.tv.ui.screen.dynamics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.tv.network.model.dynamic.DynamicItem
import com.bilibili.tv.repository.DynamicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DynamicUiState {
    object Loading : DynamicUiState()
    data class Success(
        val items: List<DynamicItem>,
        val hasMore: Boolean,
        val isAppending: Boolean = false
    ) : DynamicUiState()
    data class Error(val message: String) : DynamicUiState()
}

class DynamicViewModel : ViewModel() {
    private val repository = DynamicRepository()

    private val _uiState = MutableStateFlow<DynamicUiState>(DynamicUiState.Loading)
    val uiState: StateFlow<DynamicUiState> = _uiState.asStateFlow()

    private var currentOffset: String = ""
    private var allItems = mutableListOf<DynamicItem>()
    private var isFetching = false

    init {
        loadDynamics(isRefresh = true)
    }

    fun loadDynamics(isRefresh: Boolean = false) {
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            
            if (isRefresh) {
                currentOffset = ""
                allItems.clear()
                _uiState.value = DynamicUiState.Loading
            } else {
                val currentState = _uiState.value
                if (currentState is DynamicUiState.Success) {
                    if (!currentState.hasMore) {
                        isFetching = false
                        return@launch
                    }
                    _uiState.value = currentState.copy(isAppending = true)
                }
            }

            val result = repository.getDynamicFeed(currentOffset)
            result.onSuccess { data ->
                val newItems = data.items.filter { it.visible } // Filter out hidden posts
                allItems.addAll(newItems)
                currentOffset = data.offset
                val hasMore = data.hasMore // 已从 Int 改为 Boolean

                if (allItems.isNotEmpty()) {
                    _uiState.value = DynamicUiState.Success(
                        items = allItems.toList(),
                        hasMore = hasMore,
                        isAppending = false
                    )
                } else {
                    _uiState.value = DynamicUiState.Error("暂无订阅动态")
                }
            }.onFailure { error ->
                if (allItems.isEmpty()) {
                    _uiState.value = DynamicUiState.Error(error.localizedMessage ?: "拉取动态失败")
                } else {
                    // Silent fail for pagination to not destroy current view
                    _uiState.value = DynamicUiState.Success(
                        items = allItems.toList(),
                        hasMore = true,
                        isAppending = false
                    )
                }
            }
            
            isFetching = false
        }
    }
}

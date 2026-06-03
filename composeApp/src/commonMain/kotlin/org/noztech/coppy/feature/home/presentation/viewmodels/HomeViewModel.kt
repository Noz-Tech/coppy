package org.noztech.coppy.feature.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.noztech.EntryItem
import org.noztech.coppy.feature.home.domain.usecase.DeleteItemUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetGroupsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetHiddenItemsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemsUseCase
import org.noztech.coppy.feature.home.domain.usecase.ToggleItemVisibilityUseCase

class HomeViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val getItemsUseCase: GetItemsUseCase,
    private val getHiddenItemsUseCase: GetHiddenItemsUseCase,
    private val toggleItemVisibilityUseCase: ToggleItemVisibilityUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
) : ViewModel() {

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId = _selectedGroupId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val groups = getGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items = getItemsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenItems = getHiddenItemsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(FlowPreview::class)
    val filteredItems = combine(items, selectedGroupId, searchQuery) { allItems, selectedId, query ->
        val lowerQuery = query.trim().lowercase()

        // Apply group filter first
        var filtered = if (selectedId == null) {
            allItems
        } else {
            allItems.filter { it.groupId == selectedId }
        }

        // Apply search filter
        if (lowerQuery.isNotBlank()) {
            filtered = filtered.filter { it.matches(lowerQuery) }
        }

        filtered
    }
        .debounce(100) // delay to smooth out last char backspace lag
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private fun EntryItem.matches(query: String): Boolean {
        return title.lowercase().contains(query) ||
                value_.orEmpty().lowercase().contains(query)
    }
    fun selectGroup(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun hideItem(id: Long) {
        viewModelScope.launch {
            try {
                toggleItemVisibilityUseCase(id)
            } catch (e: Exception) {
                println("Failed to update visibility for item $id: ${e.message}")
            }
        }
    }

    fun unhideItem(id: Long) {
        hideItem(id)
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            try {
                deleteItemUseCase(id)
            } catch (e: Exception) {
                println("Failed to delete item $id: ${e.message}")
            }
        }
    }
}

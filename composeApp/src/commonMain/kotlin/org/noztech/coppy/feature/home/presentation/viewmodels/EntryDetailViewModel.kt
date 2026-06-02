package org.noztech.coppy.feature.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.noztech.EntryItem
import org.noztech.coppy.feature.home.domain.usecase.GetItemByIdUseCase
import org.noztech.coppy.feature.home.domain.usecase.ToggleItemVisibilityUseCase

class EntryDetailViewModel(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val toggleItemVisibilityUseCase: ToggleItemVisibilityUseCase,
) : ViewModel() {

    private val _entry = MutableStateFlow<EntryItem?>(null)
    val entry = _entry.asStateFlow()

    fun load(id: Long) {
        _entry.value = getItemByIdUseCase(id)
    }

    fun toggleVisibility() {
        val current = _entry.value ?: return
        viewModelScope.launch {
            toggleItemVisibilityUseCase(current.id)
            load(current.id)
        }
    }
}

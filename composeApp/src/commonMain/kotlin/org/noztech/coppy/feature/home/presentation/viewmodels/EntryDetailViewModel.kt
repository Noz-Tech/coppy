package org.noztech.coppy.feature.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.noztech.EntryField
import org.noztech.EntryItem
import org.noztech.coppy.feature.home.domain.usecase.GetEntryFieldsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemByIdUseCase
import org.noztech.coppy.feature.home.domain.usecase.ToggleItemVisibilityUseCase

class EntryDetailViewModel(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val toggleItemVisibilityUseCase: ToggleItemVisibilityUseCase,
    private val getEntryFieldsUseCase: GetEntryFieldsUseCase,
) : ViewModel() {

    private val _entry = MutableStateFlow<EntryItem?>(null)
    val entry = _entry.asStateFlow()
    private val _fields = MutableStateFlow<List<EntryField>>(emptyList())
    val fields = _fields.asStateFlow()

    fun load(id: Long) {
        val item = getItemByIdUseCase(id)
        _entry.value = item
        _fields.value = if (item != null) {
            getEntryFieldsUseCase(id)
        } else {
            emptyList()
        }
    }

    fun toggleVisibility() {
        val current = _entry.value ?: return
        viewModelScope.launch {
            toggleItemVisibilityUseCase(current.id)
            load(current.id)
        }
    }
}

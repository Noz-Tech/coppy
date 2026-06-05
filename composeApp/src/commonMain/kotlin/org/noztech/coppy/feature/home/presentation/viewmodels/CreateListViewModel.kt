package org.noztech.coppy.feature.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.noztech.EntryGroup
import org.noztech.EntryField
import org.noztech.EntryItem
import org.noztech.coppy.feature.home.domain.usecase.CreateGroupUseCase
import org.noztech.coppy.feature.home.domain.usecase.CreateItemUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetEntryFieldsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetGroupsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemByIdUseCase
import org.noztech.coppy.feature.home.domain.model.EntryFieldInput
import org.noztech.coppy.feature.home.domain.usecase.ReplaceEntryFieldsUseCase
import org.noztech.coppy.feature.home.domain.usecase.UpdateItemUseCase
import org.noztech.coppy.feature.home.presentation.SaveState

class CreateListViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val replaceEntryFieldsUseCase: ReplaceEntryFieldsUseCase,
    private val getEntryFieldsUseCase: GetEntryFieldsUseCase,
) : ViewModel() {

    fun getItemById(itemId: Long): EntryItem? {
        return getItemByIdUseCase(itemId)
    }

    fun getFieldsByEntryId(itemId: Long): List<EntryField> {
        return getEntryFieldsUseCase(itemId)
    }


    val groups: StateFlow<List<EntryGroup>> = getGroupsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId = _selectedGroupId.asStateFlow()

    fun selectGroup(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun clearSelectedGroup() {
        _selectedGroupId.value = null
    }

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    fun showValidationError(message: String) {
        _saveState.value = SaveState.Error(message)
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            try {
                createGroupUseCase(name)
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Unable to create folder")
            }
        }
    }

    fun saveItem(
        title: String,
        entryType: String,
        groupId: Long?,
        existingItemId: Long? = null,
        fields: List<EntryFieldInput> = emptyList(),
    ) {
        viewModelScope.launch {
            if (title.isBlank() || fields.isEmpty()) {
                _saveState.value = SaveState.Error("Name and at least one field are required")
                return@launch
            }

            _saveState.value = SaveState.Loading

            try {
                if (existingItemId != null) {
                    updateItemUseCase(existingItemId, groupId, title, entryType)
                    replaceEntryFieldsUseCase(existingItemId, fields)
                    updateItemUseCase(existingItemId, groupId, title, entryType)
                    _saveState.value = SaveState.Success("Item updated successfully")
                } else {
                    val newItemId = createItemUseCase(groupId, title, entryType)
                    if (newItemId <= 0) {
                        _saveState.value = SaveState.Error("Unable to save entry fields")
                        return@launch
                    }
                    replaceEntryFieldsUseCase(newItemId, fields)
                    val savedFields = getEntryFieldsUseCase(newItemId)
                    if (savedFields.isEmpty()) {
                        _saveState.value = SaveState.Error("Unable to save entry fields")
                        return@launch
                    }
                    updateItemUseCase(newItemId, groupId, title, entryType)
                    _saveState.value = SaveState.Success("Item created successfully")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

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
import org.noztech.EntryItem
import org.noztech.coppy.feature.home.domain.usecase.CreateGroupUseCase
import org.noztech.coppy.feature.home.domain.usecase.CreateItemUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetGroupsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemByIdUseCase
import org.noztech.coppy.feature.home.domain.usecase.UpdateItemUseCase
import org.noztech.coppy.feature.home.presentation.SaveState

class CreateListViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase
) : ViewModel() {

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    fun setImagePath(path: String?) {
        _imagePath.value = path
    }

    fun getItemById(itemId: Long): EntryItem? {
        return getItemByIdUseCase(itemId)
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
        value: String,
        entryType: String,
        issuer: String?,
        expiresAt: String?,
        securityCode: String?,
        existingItemId: Long? = null
    ) {
        viewModelScope.launch {
            if (title.isBlank() || value.isBlank()) {
                _saveState.value = SaveState.Error("Name and Value cannot be empty")
                return@launch
            }

            _saveState.value = SaveState.Loading

            try {
                val groupId = _selectedGroupId.value
                if (existingItemId != null) {
                    updateItemUseCase(existingItemId, groupId, title, value, entryType, issuer, expiresAt, securityCode)
                    _saveState.value = SaveState.Success("Item updated successfully")
                } else {
                    createItemUseCase(groupId, title, value, entryType, issuer, expiresAt, securityCode)
                    _saveState.value = SaveState.Success("Item created successfully")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Unknown error")
            }
        }
    }

    var openCameraAction: ((onResult: (String?) -> Unit) -> Unit)? = null
    fun openCamera() {
        openCameraAction?.invoke { path -> setImagePath(path) }
    }
}

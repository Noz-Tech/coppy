package org.noztek.coppy.feature.home.presentation

sealed class SaveState {
    object Idle : SaveState()
    object Loading : SaveState()
    data class Success(val message: String? = null) : SaveState()
    data class Error(val error: String) : SaveState()
}
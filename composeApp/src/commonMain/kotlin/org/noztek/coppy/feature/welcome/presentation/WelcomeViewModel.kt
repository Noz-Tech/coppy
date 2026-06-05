package org.noztek.coppy.feature.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.noztek.coppy.core.AppSettings
import kotlin.time.ExperimentalTime

class WelcomeViewModel(
    private val appSettings: AppSettings
): ViewModel() {
    @OptIn(ExperimentalTime::class)
    fun firstLaunch(){
        viewModelScope.launch {
            appSettings.setFirstLaunch()
        }
    }
}
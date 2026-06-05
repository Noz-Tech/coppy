package org.noztek.coppy.core

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettings(private val settings: Settings) {
    companion object {
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_LOCK_ON_LAUNCH = "lock_on_launch"
        private const val KEY_BIOMETRIC_PERMISSION_ASKED = "biometric_permission_asked"
        private const val KEY_BIOMETRIC_ON_REVEAL = "biometric_on_reveal"
        private const val KEY_BIOMETRIC_ON_COPY = "biometric_on_copy"
        private const val KEY_BIOMETRIC_ON_SHARE = "biometric_on_share"
        private const val KEY_BIOMETRIC_ON_HIDDEN_ITEMS = "biometric_on_hidden_items"
        private const val KEY_SHOW_HIDDEN_ITEMS = "show_hidden_items"
        private const val KEY_SAMPLE_DATA_SEEDED = "sample_data_seeded"
    }

    private val _showHiddenItems = MutableStateFlow(settings.getBoolean(KEY_SHOW_HIDDEN_ITEMS, false))
    val showHiddenItems = _showHiddenItems.asStateFlow()

    fun setFirstLaunch() {
        settings.putBoolean(KEY_IS_FIRST_LAUNCH, false)
    }

    fun resetFirstLaunch() {
        settings.putBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun setLockOnLaunch(isEnabled: Boolean) {
        settings.putBoolean(KEY_LOCK_ON_LAUNCH, isEnabled)
    }

    fun setBiometricPermissionAsked() {
        settings.putBoolean(KEY_BIOMETRIC_PERMISSION_ASKED, true)
    }

    fun setBiometricOnReveal(isEnabled: Boolean) {
        settings.putBoolean(KEY_BIOMETRIC_ON_REVEAL, isEnabled)
    }

    fun setBiometricOnCopy(isEnabled: Boolean) {
        settings.putBoolean(KEY_BIOMETRIC_ON_COPY, isEnabled)
    }

    fun setBiometricOnShare(isEnabled: Boolean) {
        settings.putBoolean(KEY_BIOMETRIC_ON_SHARE, isEnabled)
    }

    fun setBiometricOnHiddenItems(isEnabled: Boolean) {
        settings.putBoolean(KEY_BIOMETRIC_ON_HIDDEN_ITEMS, isEnabled)
    }

    fun setShowHiddenItems(isEnabled: Boolean) {
        settings.putBoolean(KEY_SHOW_HIDDEN_ITEMS, isEnabled)
        _showHiddenItems.value = isEnabled
    }

    fun setSampleDataSeeded() {
        settings.putBoolean(KEY_SAMPLE_DATA_SEEDED, true)
    }

    fun resetSampleDataSeeded() {
        settings.putBoolean(KEY_SAMPLE_DATA_SEEDED, false)
    }

    fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun isLockOnLaunchEnabled(): Boolean {
        return settings.getBoolean(KEY_LOCK_ON_LAUNCH, false)
    }

    fun isBiometricPermissionAsked(): Boolean {
        return settings.getBoolean(KEY_BIOMETRIC_PERMISSION_ASKED, false)
    }

    fun isBiometricOnRevealEnabled(): Boolean {
        return settings.getBoolean(KEY_BIOMETRIC_ON_REVEAL, false)
    }

    fun isBiometricOnCopyEnabled(): Boolean {
        return settings.getBoolean(KEY_BIOMETRIC_ON_COPY, false)
    }

    fun isBiometricOnShareEnabled(): Boolean {
        return settings.getBoolean(KEY_BIOMETRIC_ON_SHARE, false)
    }

    fun isBiometricOnHiddenItemsEnabled(): Boolean {
        return settings.getBoolean(KEY_BIOMETRIC_ON_HIDDEN_ITEMS, true)
    }

    fun isShowHiddenItemsEnabled(): Boolean {
        return settings.getBoolean(KEY_SHOW_HIDDEN_ITEMS, false)
    }

    fun isSampleDataSeeded(): Boolean {
        return settings.getBoolean(KEY_SAMPLE_DATA_SEEDED, false)
    }
}

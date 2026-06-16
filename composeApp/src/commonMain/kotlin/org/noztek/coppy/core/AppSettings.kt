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
        private const val KEY_HOME_ONBOARDING_COMPLETED = "home_onboarding_completed"
        private const val KEY_HOME_ONBOARDING_REPLAY_PENDING = "home_onboarding_replay_pending"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ENTRY_CREATE_COUNT = "entry_create_count"
        private const val KEY_COPY_ACTION_COUNT = "copy_action_count"
        private const val KEY_RATE_PROMPT_PENDING = "rate_prompt_pending"
        private const val KEY_RATE_PROMPT_HANDLED = "rate_prompt_handled"
    }

    enum class ThemeMode {
        SYSTEM,
        LIGHT,
        DARK,
    }

    private val _lockOnLaunchEnabled = MutableStateFlow(settings.getBoolean(KEY_LOCK_ON_LAUNCH, false))
    val lockOnLaunchEnabled = _lockOnLaunchEnabled.asStateFlow()
    private val _showHiddenItems = MutableStateFlow(settings.getBoolean(KEY_SHOW_HIDDEN_ITEMS, false))
    val showHiddenItems = _showHiddenItems.asStateFlow()
    private val _homeOnboardingCompleted = MutableStateFlow(
        settings.getBoolean(KEY_HOME_ONBOARDING_COMPLETED, false)
    )
    val homeOnboardingCompleted = _homeOnboardingCompleted.asStateFlow()
    private val _homeOnboardingReplayPending = MutableStateFlow(
        settings.getBoolean(KEY_HOME_ONBOARDING_REPLAY_PENDING, false)
    )
    val homeOnboardingReplayPending = _homeOnboardingReplayPending.asStateFlow()
    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode = _themeMode.asStateFlow()
    private val _ratePromptPending = MutableStateFlow(settings.getBoolean(KEY_RATE_PROMPT_PENDING, false))
    val ratePromptPending = _ratePromptPending.asStateFlow()

    fun setFirstLaunch() {
        settings.putBoolean(KEY_IS_FIRST_LAUNCH, false)
    }

    fun resetFirstLaunch() {
        settings.putBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun setLockOnLaunch(isEnabled: Boolean) {
        settings.putBoolean(KEY_LOCK_ON_LAUNCH, isEnabled)
        _lockOnLaunchEnabled.value = isEnabled
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

    fun completeHomeOnboarding() {
        settings.putBoolean(KEY_HOME_ONBOARDING_COMPLETED, true)
        _homeOnboardingCompleted.value = true
    }

    fun resetHomeOnboarding() {
        settings.putBoolean(KEY_HOME_ONBOARDING_COMPLETED, false)
        settings.putBoolean(KEY_HOME_ONBOARDING_REPLAY_PENDING, false)
        _homeOnboardingCompleted.value = false
        _homeOnboardingReplayPending.value = false
    }

    fun requestHomeOnboardingReplay() {
        settings.putBoolean(KEY_HOME_ONBOARDING_REPLAY_PENDING, true)
        _homeOnboardingReplayPending.value = true
    }

    fun clearHomeOnboardingReplayRequest() {
        settings.putBoolean(KEY_HOME_ONBOARDING_REPLAY_PENDING, false)
        _homeOnboardingReplayPending.value = false
    }

    fun setThemeMode(mode: ThemeMode) {
        settings.putString(KEY_THEME_MODE, mode.name)
        _themeMode.value = mode
    }

    fun recordEntryCreated() {
        val nextCount = settings.getInt(KEY_ENTRY_CREATE_COUNT, 0) + 1
        settings.putInt(KEY_ENTRY_CREATE_COUNT, nextCount)
        updateRatePromptEligibility(
            entryCreateCount = nextCount,
            copyActionCount = settings.getInt(KEY_COPY_ACTION_COUNT, 0)
        )
    }

    fun recordCopyAction() {
        val nextCount = settings.getInt(KEY_COPY_ACTION_COUNT, 0) + 1
        settings.putInt(KEY_COPY_ACTION_COUNT, nextCount)
        updateRatePromptEligibility(
            entryCreateCount = settings.getInt(KEY_ENTRY_CREATE_COUNT, 0),
            copyActionCount = nextCount
        )
    }

    fun clearRatePromptPending() {
        settings.putBoolean(KEY_RATE_PROMPT_PENDING, false)
        _ratePromptPending.value = false
    }

    fun markRatePromptHandled() {
        settings.putBoolean(KEY_RATE_PROMPT_HANDLED, true)
        settings.putBoolean(KEY_RATE_PROMPT_PENDING, false)
        _ratePromptPending.value = false
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

    fun isHomeOnboardingCompleted(): Boolean {
        return settings.getBoolean(KEY_HOME_ONBOARDING_COMPLETED, false)
    }

    fun isHomeOnboardingReplayPending(): Boolean {
        return settings.getBoolean(KEY_HOME_ONBOARDING_REPLAY_PENDING, false)
    }

    fun resetRatePromptState() {
        settings.putInt(KEY_ENTRY_CREATE_COUNT, 0)
        settings.putInt(KEY_COPY_ACTION_COUNT, 0)
        settings.putBoolean(KEY_RATE_PROMPT_PENDING, false)
        settings.putBoolean(KEY_RATE_PROMPT_HANDLED, false)
        _ratePromptPending.value = false
    }

    private fun readThemeMode(): ThemeMode {
        val raw = settings.getStringOrNull(KEY_THEME_MODE) ?: return ThemeMode.SYSTEM
        return ThemeMode.entries.firstOrNull { it.name == raw } ?: ThemeMode.SYSTEM
    }

    private fun updateRatePromptEligibility(
        entryCreateCount: Int,
        copyActionCount: Int,
    ) {
        if (settings.getBoolean(KEY_RATE_PROMPT_HANDLED, false)) return
        val eligible = entryCreateCount >= 2 || copyActionCount >= 3
        if (eligible) {
            settings.putBoolean(KEY_RATE_PROMPT_PENDING, true)
            _ratePromptPending.value = true
        }
    }
}

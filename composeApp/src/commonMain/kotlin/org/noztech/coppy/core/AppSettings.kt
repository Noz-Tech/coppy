package org.noztech.coppy.core

import com.russhwolf.settings.Settings

class AppSettings(private val settings: Settings) {
    companion object {
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_LOCK_ON_LAUNCH = "lock_on_launch"
        private const val KEY_BIOMETRIC_ON_REVEAL = "biometric_on_reveal"
        private const val KEY_BIOMETRIC_ON_COPY = "biometric_on_copy"
        private const val KEY_BIOMETRIC_ON_SHARE = "biometric_on_share"
    }

    fun setFirstLaunch() {
        settings.putBoolean(KEY_IS_FIRST_LAUNCH, false)
    }

    fun setLockOnLaunch(isEnabled: Boolean) {
        settings.putBoolean(KEY_LOCK_ON_LAUNCH, isEnabled)
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

    fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun isLockOnLaunchEnabled(): Boolean {
        return settings.getBoolean(KEY_LOCK_ON_LAUNCH, false)
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
}

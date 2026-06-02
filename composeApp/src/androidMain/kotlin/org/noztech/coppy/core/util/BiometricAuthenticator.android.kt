package org.noztech.coppy.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON
import androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS
import androidx.biometric.BiometricPrompt.ERROR_HW_NOT_PRESENT
import androidx.biometric.BiometricPrompt.ERROR_HW_UNAVAILABLE
import androidx.fragment.app.FragmentActivity
import org.noztech.coppy.core.MyActivityProvider
import java.util.concurrent.Executor

actual class BiometricAuthenticator {
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

    private fun activity(): FragmentActivity? = MyActivityProvider.activity as? FragmentActivity

    actual fun canAuthenticate(): BiometricAuthStatus {
        val activity = activity() ?: return BiometricAuthStatus.UNKNOWN_ERROR
        val biometricManager = BiometricManager.from(activity)
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAuthStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthStatus.UNKNOWN_ERROR
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthStatus.NOT_ENROLLED
            else -> BiometricAuthStatus.UNKNOWN_ERROR
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    actual fun authenticate(title: String, description: String, onResult: (BiometricAuthResult) -> Unit) {
        val activity = activity() ?: run {
            onResult(BiometricAuthResult.Error)
            return
        }
        val executor: Executor = activity.mainExecutor
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        ERROR_USER_CANCELED, ERROR_CANCELED, ERROR_NEGATIVE_BUTTON -> onResult(BiometricAuthResult.UserCancelled)
                        ERROR_NO_BIOMETRICS -> onResult(BiometricAuthResult.UserCancelled)
                        ERROR_HW_NOT_PRESENT, ERROR_HW_UNAVAILABLE -> onResult(BiometricAuthResult.Error)
                        else -> onResult(BiometricAuthResult.Error)
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(BiometricAuthResult.Success)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onResult(BiometricAuthResult.Failure)
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}

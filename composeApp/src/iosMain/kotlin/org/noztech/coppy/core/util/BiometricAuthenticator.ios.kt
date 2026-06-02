package org.noztech.coppy.core.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class BiometricAuthenticator {

    @OptIn(ExperimentalForeignApi::class)
    actual fun canAuthenticate(): BiometricAuthStatus {
        val context = LAContext()
        return if (context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)) {
            BiometricAuthStatus.AVAILABLE
        } else {
            BiometricAuthStatus.UNKNOWN_ERROR
        }
    }

    actual fun authenticate(title: String, description: String, onResult: (BiometricAuthResult) -> Unit) {
        val context = LAContext()
        context.evaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            description.ifBlank { title },
            reply = { success, error ->
                dispatch_async(dispatch_get_main_queue()) {
                    if (success) {
                        onResult(BiometricAuthResult.Success)
                    } else {
                        when (error?.code) {
                            LAErrorUserCancel -> onResult(BiometricAuthResult.UserCancelled)
                            else -> onResult(BiometricAuthResult.Error)
                        }
                    }
                }
            },
        )
    }
}

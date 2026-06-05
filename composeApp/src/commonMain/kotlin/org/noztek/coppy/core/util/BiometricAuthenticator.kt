package org.noztek.coppy.core.util

expect class BiometricAuthenticator() {
    fun canAuthenticate(): BiometricAuthStatus
    fun authenticate(title: String, description: String, onResult: (BiometricAuthResult) -> Unit)
}

enum class BiometricAuthStatus {
    AVAILABLE, NOT_ENROLLED, NO_HARDWARE, UNKNOWN_ERROR
}

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    object Failure : BiometricAuthResult()
    object Error : BiometricAuthResult()
    object UserCancelled : BiometricAuthResult()
}

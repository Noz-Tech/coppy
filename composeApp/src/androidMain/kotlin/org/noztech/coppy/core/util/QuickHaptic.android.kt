package org.noztech.coppy.core.util

import android.view.HapticFeedbackConstants
import org.noztech.coppy.core.MyActivityProvider

actual fun QuickHaptic() {
    val activity = MyActivityProvider.activity ?: return
    activity.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
}

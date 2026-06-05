package org.noztek.coppy.core.util

import android.view.HapticFeedbackConstants
import org.noztek.coppy.core.MyActivityProvider

actual fun QuickHaptic() {
    val activity = MyActivityProvider.activity ?: return
    activity.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
}

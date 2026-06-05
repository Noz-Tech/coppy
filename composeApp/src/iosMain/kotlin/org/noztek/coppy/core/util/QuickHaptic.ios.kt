package org.noztek.coppy.core.util

import platform.UIKit.UISelectionFeedbackGenerator

actual fun QuickHaptic() {
    val generator = UISelectionFeedbackGenerator()
    generator.prepare()
    generator.selectionChanged()
}

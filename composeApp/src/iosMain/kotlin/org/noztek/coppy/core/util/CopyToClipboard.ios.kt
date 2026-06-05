package org.noztek.coppy.core.util

import platform.UIKit.UIPasteboard

actual fun CopyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}
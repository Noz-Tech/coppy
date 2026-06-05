package org.noztek.coppy.core.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UINavigationController
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.UITabBarController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun ShareText(text: String) {
    dispatch_async(dispatch_get_main_queue()) {
        val viewController = topViewController() ?: return@dispatch_async
        val activityViewController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null,
        )

        viewController.presentViewController(activityViewController, animated = true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun topViewController(): UIViewController? {
    val windowScene = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }

    val rootViewController = windowScene?.windows
        ?.filterIsInstance<UIWindow>()
        ?.firstOrNull { it.keyWindow }
        ?.rootViewController

    return rootViewController?.visibleViewController()
}

private tailrec fun UIViewController.visibleViewController(): UIViewController {
    val presented = presentedViewController
    if (presented != null) return presented.visibleViewController()

    return when (this) {
        is UINavigationController -> visibleViewController?.visibleViewController() ?: this
        is UITabBarController -> selectedViewController?.visibleViewController() ?: this
        else -> this
    }
}

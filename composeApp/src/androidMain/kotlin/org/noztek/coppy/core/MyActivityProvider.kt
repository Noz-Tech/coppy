package org.noztek.coppy.core

import android.app.Activity
import java.lang.ref.WeakReference

object MyActivityProvider {
    private var currentActivityRef: WeakReference<Activity>? = null

    var activity: Activity?
        get() = currentActivityRef?.get()
        set(value) {
            currentActivityRef = if (value != null) WeakReference(value) else null
        }
}
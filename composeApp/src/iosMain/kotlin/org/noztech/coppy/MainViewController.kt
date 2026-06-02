package org.noztech.coppy

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.dsl.module
import org.noztech.coppy.core.database.DatabaseDriverFactory
import org.noztech.coppy.di.initKoin

private var isKoinStarted = false

fun MainViewController(): platform.UIKit.UIViewController {
    initKoinIfNeeded()
    return ComposeUIViewController { App() }
}

private fun initKoinIfNeeded() {
    if (isKoinStarted) return

    initKoin {
        modules(
            module {
                single { DatabaseDriverFactory() }
            },
        )
    }
    isKoinStarted = true
}

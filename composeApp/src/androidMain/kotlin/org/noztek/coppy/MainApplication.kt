package org.noztek.coppy

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module
import org.noztek.coppy.core.database.DatabaseDriverFactory
import org.noztek.coppy.di.initKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(
                module {
                    single { DatabaseDriverFactory(applicationContext) }
                }
            )
        }
    }
}
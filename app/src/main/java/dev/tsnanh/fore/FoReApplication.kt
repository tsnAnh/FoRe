package dev.tsnanh.fore

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FoReApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()

            androidContext(this@FoReApplication)

            androidFileProperties()

            modules(dev.tsnanh.fore.di.modules)
        }
    }
}
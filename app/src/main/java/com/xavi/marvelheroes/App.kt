package com.xavi.marvelheroes

import android.app.Application
import com.xavi.marvelheroes.di.characterListModule
import com.xavi.marvelheroes.di.mapperModule
import com.xavi.marvelheroes.di.retrofitModule
import com.xavi.marvelheroes.di.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    mapperModule,
                    roomModule,
                    retrofitModule,
                    characterListModule
                )
            )
        }
    }
}

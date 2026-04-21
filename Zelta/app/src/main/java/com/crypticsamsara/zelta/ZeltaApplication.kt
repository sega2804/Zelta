package com.crypticsamsara.zelta

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ZeltaApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
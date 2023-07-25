package com.handongwook.annotation

import android.app.Application
import timber.log.Timber

class AnnotationApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
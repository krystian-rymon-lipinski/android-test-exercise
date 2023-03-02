package com.krystianrymonlipinski.testexercise

import android.app.Application
import timber.log.Timber

class TestExerciseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
package com.kseniabl.tasksapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TasksApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
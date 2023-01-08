package com.example.ipctest

import android.app.Application
import android.content.Intent
import com.example.ipctest.server.RemoteService
import java.util.concurrent.Executors


class ServerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Executors.newSingleThreadExecutor().execute {
            while (true) {
                RemoteService.notifyTime()
                Thread.sleep(500)
            }
        }

    }
}
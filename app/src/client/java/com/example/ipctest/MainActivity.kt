package com.example.ipctest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.Date
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity";
    }

    private val remoteManager = RemoteManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        remoteManager.init(this) {
            Executors.newSingleThreadExecutor().execute {
                while (true) {
                    getSingleTime()
                    Thread.sleep(600)
                }
            }

        }

    }

    private fun getSingleTime() {
        val listener = object : IListenerInterface.Stub() {
            override fun onEvent(timestamp: Long) {
                remoteManager.unregisterCallback(this);
                Log.d(TAG, "Time is ${Date(timestamp)}")
            }
        }
        remoteManager.registerCallback(Executors.newSingleThreadExecutor(), listener)
    }

}
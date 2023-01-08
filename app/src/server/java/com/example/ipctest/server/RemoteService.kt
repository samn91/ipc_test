package com.example.ipctest.server

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.ipctest.IListenerInterface
import com.example.ipctest.ITimeApi

class RemoteService : Service() {

    companion object {
        const val TAG = "RemoteService"
        private val listeners = HashSet<IListenerInterface>()
        fun notifyTime(): Unit {
            val time = System.currentTimeMillis()
            Log.d(TAG, "We have ${listeners.size} listeners")
            synchronized(listeners) {
                for (listener in ArrayList(listeners)) {
                    try {
                        listener.onEvent(time)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        listeners.remove(listener)
                    }
                }
            }
        }
    }

    private val binder = object : ITimeApi.Stub() {

        override fun registerCallback(lisener: IListenerInterface) {
            synchronized(listeners) {
                Log.d(TAG, "registerCallback: ")
                listeners.add(lisener)
                lisener.onEvent(System.currentTimeMillis())
            }
        }

        override fun unregisterCallback(lisener: IListenerInterface) {
            synchronized(listeners) {
                Log.d(TAG, "unregisterCallback: ")
                listeners.remove(lisener)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")

    }


}
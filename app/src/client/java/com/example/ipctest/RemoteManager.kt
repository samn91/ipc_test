package com.example.ipctest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import java.util.concurrent.Executor

class RemoteManager {
    companion object {
        const val TAG = "RemoteManager"
    }

    private var iRemoteService: ITimeApi? = null
    private val listenerMap = mutableMapOf<IListenerInterface, ListenerProxy>()
    private var onInitFinished: (() -> Unit)? = null
    private val mConnection = object : ServiceConnection {

        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected: ")
            iRemoteService = ITimeApi.Stub.asInterface(service)
            onInitFinished?.invoke()
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            iRemoteService = null
        }
    }

    fun init(context: Context, onInitFinished: () -> Unit) {
        val intent = Intent("com.example.ipctest.server.RemoteService.BIND")
            .setPackage("com.example.ipctest.server");
        val bindService = context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "init: $bindService")
        this.onInitFinished = onInitFinished
    }

    fun registerCallback(executor: Executor, listener: IListenerInterface) {
        Log.d(TAG, "registerCallback: ");

        val stub = ListenerProxy(listener, executor)
        listenerMap[listener] = stub

        try {
            iRemoteService?.registerCallback(stub)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun unregisterCallback(listener: IListenerInterface) {
        Log.d(TAG, "unregisterCallback: ");
        try {
            val removedListener = listenerMap.remove(listener)
                ?: throw IllegalArgumentException("Listener was not registered.")
            removedListener.unregister()
            iRemoteService?.unregisterCallback(removedListener)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    private class ListenerProxy(
        private val listener: IListenerInterface, private val executor: Executor
    ) : IListenerInterface.Stub() {

        private var isUnregistered = false
        override fun onEvent(time: Long) {

            executor.execute {
                if (isUnregistered)
                    listener.onEvent(time)
            }
        }

        fun unregister(): Unit {
            isUnregistered = true
        }
    }
}
package com.example.ipctest;

import com.example.ipctest.IListenerInterface;

interface ITimeApi {
 void registerCallback(IListenerInterface lisener);
 void unregisterCallback(IListenerInterface lisener);
}
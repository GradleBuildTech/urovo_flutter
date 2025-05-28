package com.example.urovo_flutter.module.urovo.service

abstract class UrovoBaseService {
    var isRunning = false

    abstract fun onStart(arg: Any? = null, errorCallBack: ((String) -> Unit)? = null)
    abstract fun onStop()
}
package com.example.urovo_flutter.module

abstract class BaseService {
    var isRunning = false

    abstract fun onStart(arg: Any? = null, errorCallBack: ((String) -> Unit)? = null)
    abstract fun onStop()
}
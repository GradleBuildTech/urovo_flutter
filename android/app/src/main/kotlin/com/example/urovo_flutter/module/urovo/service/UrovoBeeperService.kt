package com.example.urovo_flutter.module.urovo.service

import com.example.urovo_flutter.model.toBeeperModel
import com.urovo.sdk.beeper.BeeperImpl

internal class UrovoBeeperService(
    private val beeperProvider: BeeperImpl
): UrovoBaseService() {
    override fun onStart(arg: Any?, errorCallBack: ((String) -> Unit)?) {
        if(arg is Map<*, *>) {
            val model = arg.toBeeperModel()
            beeperProvider.startBeep(model.cnts ?: 0, model.msecTime ?: 0)
        }
        errorCallBack?.invoke("beep error")
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }


}
package com.example.urovo_flutter.service

import com.example.urovo_flutter.model.toBeeperModel
import com.urovo.sdk.beeper.BeeperImpl

internal class BeeperService(
    private val beeperProvider: BeeperImpl
): BaseService() {
    companion object {
        const val METHOD_BEEP = "beep"
    }

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
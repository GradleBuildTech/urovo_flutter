package com.example.urovo_flutter

import android.content.Context
import android.util.Log
import com.example.urovo_flutter.module.BaseModule
import com.example.urovo_flutter.module.urovo.UrovoModule
import com.example.urovo_flutter.utils.ChannelTag
import com.example.urovo_flutter.utils.DeviceEnum
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class AppService private constructor() {

    private var isRunning = false
    private var device: DeviceEnum = DeviceEnum.UROVO
    // Set the default module to UrovoModule
    private var module: BaseModule = UrovoModule()

    companion object {
        @Volatile
        private var instance: AppService? = null

        fun getInstance(): AppService {
            return instance ?: synchronized(this) {
                instance ?: AppService().also { instance = it }
            }
        }
    }

    fun startService() {
        isRunning = true
    }

    fun stopService() {
        isRunning = false
    }

    private fun setDevice(device: DeviceEnum) {
        this.device = device
        module = when (device) {
            DeviceEnum.UROVO -> UrovoModule()
            DeviceEnum.OTHER -> UrovoModule()
        }
    }

    fun doAction(
        context: Context,
        method: String,
        argument: Any,
        result: MethodChannel.Result
    ) {
        when (method) {
            ChannelTag.GET_DEVICE_METHOD -> {
                if (argument is String) {
                    setDevice(DeviceEnum.fromValue(argument))
                    result.success(device.value)
                } else {
                    result.error("Invalid Argument", "Expected a String for device type", null)
                }
                return
            }

            ChannelTag.PRINT_METHOD -> {
                module.printMethod(context, argument, errorCallBack = {
                    Log.e("AppService", "Print error: $it")
                    result.error("Print Error", it, null)
                })
            }

            ChannelTag.BEEP_METHOD -> {
                module.beepMethod(context, argument, errorCallBack = {
                    result.error("Beep Error", it, null)
                })
            }

            else -> result.notImplemented()
        }
    }

    fun listenScannerStream(context: Context): EventChannel.StreamHandler {
        return module.scannerStream(context)
    }
}

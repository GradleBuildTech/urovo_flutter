package com.example.urovo_flutter

import android.content.Context
import com.example.urovo_flutter.module.BaseModule
import com.example.urovo_flutter.module.urovo.UrovoModule
import com.example.urovo_flutter.utils.ChannelTag
import com.example.urovo_flutter.utils.DeviceEnum
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

/**
 * AppService is a singleton service class that manages the state of the application.
 * It can be used to start and stop the service, and to set the device type.
 */
class AppService {
    companion object {
        private var instance: AppService? = null

        fun getInstance(): AppService {
            if (instance == null) {
                instance = AppService()
            }
            return instance!!
        }
    }

    // Add any properties or methods needed for the service
    private var isRunning: Boolean = false

    private var device: DeviceEnum = DeviceEnum.UROVO

    private var module: BaseModule? = null

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
            DeviceEnum.OTHER -> null
        }
    }


    fun doAction(
        context: Context,
        method: String,
        argument: Any,
        result: MethodChannel.Result
    ) {

        if (method == ChannelTag.GET_DEVICE_METHOD ) {
            if( argument is String) {
                setDevice(DeviceEnum.fromValue(argument))
                result.success(device.value)
            } else {
                result.error("Invalid Argument", "Expected a String for device type", null)
            }
            return
        }

        if (module == null) {
            result.error("Module not found", "No module found for device: ${device.value}", null)
            return
        }

        when (method) {
            ChannelTag.PRINT_METHOD -> {
                module?.printMethod(context, errorCallBack = { error ->
                    result.error("Print Error", error, null)
                })
            }

            ChannelTag.BEEP_METHOD -> {
                module?.beepMethod(context, argument, errorCallBack = { error ->
                    result.error("Beep Error", error, null)
                })
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    fun listenScannerStream(context: Context): EventChannel.StreamHandler {
        if (module == null) {
            throw IllegalStateException("Module not initialized. Please set the device type first.")
        }
        return module?.scannerStream(context)
            ?: throw IllegalStateException("Module not initialized or does not support scanner stream")
    }

}
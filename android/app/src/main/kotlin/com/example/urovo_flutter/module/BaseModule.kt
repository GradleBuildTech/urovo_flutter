package com.example.urovo_flutter.module

import android.content.Context
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

abstract class BaseModule {
    /**
     * This method is called to perform an action based on the method name and argument provided.
     * It should be implemented by subclasses to handle specific actions.
     *
     * @param context The context in which the action is performed.
     * @param method The name of the method to be executed.
     * @param argument The argument to be passed to the method.
     * @param result The result callback to return the result of the action.
     */
    abstract fun doAction(
        context: Context,
        method: String,
        argument: Any,
        result: MethodChannel.Result
    )

    /**
     * This method is called to provide a stream handler for the scanner events.
     * It should be implemented by subclasses to handle scanner events.
     *
     * @param context The context in which the scanner stream is created.
     * @return An instance of [EventChannel.StreamHandler] that handles scanner events.
     */
    abstract fun scannerStream(context: Context): EventChannel.StreamHandler

}
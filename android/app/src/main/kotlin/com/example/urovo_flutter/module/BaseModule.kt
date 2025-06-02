package com.example.urovo_flutter.module

import android.content.Context
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

abstract class BaseModule {
    /**
     * This method is called to provide a stream handler for the scanner events.
     * It should be implemented by subclasses to handle scanner events.
     *
     * @param context The context in which the scanner stream is created.
     * @return An instance of [EventChannel.StreamHandler] that handles scanner events.
     */
    abstract fun scannerStream(context: Context): EventChannel.StreamHandler

    /**
     * This method is called to provide a stream handler for the mag card events.
     * It should be implemented by subclasses to handle mag card events.
     *
     * @param context The context in which the mag card stream is created.♦
     * @return An instance of [EventChannel.StreamHandler] that handles mag card events.
     */
    abstract fun searchMagCardStream(context: Context): EventChannel.StreamHandler

    /**
     * This method is called to provide a stream handler for the EMV events.
     * It should be implemented by subclasses to handle EMV events.
     *
     * @param context The context in which the EMV stream is created.
     * @return An instance of [EventChannel.StreamHandler] that handles EMV events.
     */
    abstract fun emvStream(context: Context): EventChannel.StreamHandler


    /**
     * This method is called to print a method.
     * It should be implemented by subclasses to handle printing functionality.
     *
     * @param context The context in which the printing is performed.
     * @param errorCallBack A callback function to handle errors during printing.
     */
    abstract fun printMethod(
        context: Context,
        argument: Any?,
        errorCallBack: (String) -> Unit
    )

    /**
     * This method is called to beep a method.
     * It should be implemented by subclasses to handle beeping functionality.
     *
     * @param context The context in which the beeping is performed.
     * @param argument The argument to be passed to the beep method.
     * @param errorCallBack A callback function to handle errors during beeping.
     */
    abstract fun beepMethod(
        context: Context,
        argument: Any,
        errorCallBack: (String) -> Unit
    )

}
package com.example.urovo_flutter.module.urovo.service.emv

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.urovo_flutter.model.EmvModel
import com.example.urovo_flutter.model.toEmvModel
import com.urovo.i9000s.api.emv.ContantPara
import com.urovo.i9000s.api.emv.EmvListener
import com.urovo.i9000s.api.emv.EmvNfcKernelApi
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Hashtable
import java.util.concurrent.Future

internal class UrovoEmvService(
    private val context: Context,
    private val mEmvNfcKernelApi: EmvNfcKernelApi
) : EventChannel.StreamHandler {

    companion object {
        private const val LOG_TAG = "💳 EmvService"
        private const val EMV_ERROR_CODE = "🐛 EmvServiceError"
    }

    private var eventSink: EventChannel.EventSink? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    private val mEmvListener: EmvListener = UrovoEmvListener()


    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        val emvModel = (arguments as? Map<*, *>)?.toEmvModel()
        if (emvModel != null) {
            mEmvNfcKernelApi.setListener(mEmvListener)
            mEmvNfcKernelApi.setContext(context)
            mEmvNfcKernelApi.LogOutEnable(1) // 0: disable kernel log 1: enable kernel log

            //Delay 100s
            startKernel(ContantPara.CheckCardMode.SWIPE_OR_INSERT_OR_TAP, emvModel)
        } else {
            Log.e(EMV_ERROR_CODE, "Invalid EmvModel received: $arguments")
            mainHandler.post {
                eventSink?.error(
                    "InvalidArguments",
                    "Expected a valid EmvModel but received null or invalid data.",
                    null
                )
            }
            mEmvNfcKernelApi.setListener(null)
        }
    }

    override fun onCancel(arguments: Any?) {
    }

    private fun startKernel(
        checkCardMode: ContantPara.CheckCardMode,
        emvModel: EmvModel
    ) {
        Log.d(LOG_TAG, "Received EmvModel: $emvModel")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = Hashtable<String, Any>()
                data["checkCardMode"] = checkCardMode //
                data["currencyCode"] = emvModel.currencyCode.toString() //682
                data["emvOption"] = emvModel.emvOption?.toOption() // START_WITH_FORCE_ONLINE
                data["cashbackAmount"] = ""
                data["checkCardTimeout"] = emvModel.cardTimeOut.toString()

                if (emvModel.isRefund) {
                    data["transactionType"] = "20"
                } else {
                    data["transactionType"] = "00" //00-goods 01-cash 09-cashback 20-refund
                }// Check Card time out .Second

                data["FallbackSwitch"] = "0";
                data["supportDRL"] = true

                data["enableBeeper"] = emvModel.enableBeeper // Enable beeper during EMV process
                data["enableTapSwipeCollision"] =
                    emvModel.enableTapSwipeCollision // Enable tap and swipe collision handling

                mEmvNfcKernelApi.startKernel(data)
            } catch (e: Exception) {
                Log.e(EMV_ERROR_CODE, "Failed to start EMV kernel: ${e.message}", e)
                eventSink?.error(
                    "EmvServiceError",
                    "Failed to start EMV kernel: ${e.message}",
                    null
                )
            }
        }.start()
    }

    inner class UrovoEmvListener : EmvListener {
        override fun onRequestSetAmount() {
            Log.d(LOG_TAG, "onRequestSetAmount called")
        }

        override fun onReturnCheckCardResult(
            checkCardResult: ContantPara.CheckCardResult?,
            hashtable: Hashtable<String, String>?
        ) {
            Log.d(
                LOG_TAG,
                "MainActivity  onReturnCheckCardResult checkCardResult =$checkCardResult"
            )
            Log.d(LOG_TAG, hashtable.toString())
            Log.d(LOG_TAG, "POS Entry Mode:" + hashtable?.get("POSEntryMode"))
            Log.d(LOG_TAG, "FallbackType:" + hashtable?.get("FallbackType"))
            mainHandler.post {
                eventSink?.success("MainActivity  onReturnCheckCardResult checkCardResult =$checkCardResult")
            }
        }

        override fun onRequestSelectApplication(p0: ArrayList<String>?) {
            Log.d(LOG_TAG, "onRequestSelectApplication called with applications: $p0")
        }

        override fun onRequestPinEntry(p0: ContantPara.PinEntrySource?) {
            Log.d(LOG_TAG, "onRequestPinEntry called with source: $p0")
        }

        override fun onRequestOfflinePinEntry(p0: ContantPara.PinEntrySource?, p1: Int) {
            Log.d(LOG_TAG, "onRequestOfflinePinEntry called with source: $p0 and length: $p1")
        }

        override fun onRequestConfirmCardno() {
            Log.d(LOG_TAG, "onRequestConfirmCardno called")
        }

        override fun onRequestFinalConfirm() {
            Log.d(LOG_TAG, "onRequestFinalConfirm called")
        }

        override fun onRequestOnlineProcess(p0: String?, p1: String?) {
            Log.d(LOG_TAG, "onRequestOnlineProcess called with p0: $p0 and p1: $p1")
        }

        override fun onReturnBatchData(p0: String?) {
            Log.d(LOG_TAG, "onReturnBatchData called with data: $p0")
        }

        override fun onReturnTransactionResult(p0: ContantPara.TransactionResult?) {
            Log.d(LOG_TAG, "onReturnTransactionResult called with result: $p0")
        }

        override fun onRequestDisplayText(p0: ContantPara.DisplayText?) {
            Log.d(LOG_TAG, "onRequestDisplayText called with text: $p0")
        }

        override fun onRequestOfflinePINVerify(
            p0: ContantPara.PinEntrySource?,
            p1: Int,
            p2: Bundle?
        ) {
            Log.d(LOG_TAG, "onRequestOfflinePINVerify called with source: $p0, length: $p1, bundle: $p2")
        }

        override fun onReturnIssuerScriptResult(p0: ContantPara.IssuerScriptResult?, p1: String?) {
            Log.d(LOG_TAG, "onReturnIssuerScriptResult called with result: $p0 and message: $p1")
        }

        override fun onNFCrequestTipsConfirm(p0: ContantPara.NfcTipMessageID?, p1: String?) {
            Log.d(LOG_TAG, "onNFCrequestTipsConfirm called with message ID: $p0 and message: $p1")
        }

        override fun onReturnNfcCardData(p0: Hashtable<String, String>?) {
            Log.d(LOG_TAG, "onReturnNfcCardData called with data: $p0")
        }

        override fun onNFCrequestOnline() {
            Log.d(LOG_TAG, "onNFCrequestOnline called")
        }

        override fun onNFCrequestImportPin(p0: Int, p1: Int, p2: String?) {
            Log.d(LOG_TAG, "onNFCrequestImportPin called with length: $p0, timeout: $p1, message: $p2")
        }

        override fun onNFCTransResult(p0: ContantPara.NfcTransResult?) {
            Log.d(LOG_TAG, "onNFCTransResult called with result: $p0")
        }

        override fun onNFCErrorInfor(p0: ContantPara.NfcErrMessageID?, p1: String?) {
            Log.e(EMV_ERROR_CODE, "onNFCErrorInfor called with error ID: $p0 and message: $p1")
        }

    }

}
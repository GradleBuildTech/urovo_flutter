package com.example.urovo_flutter.module.urovo.service

import android.content.Context
import android.os.Bundle
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

internal class UrovoEmvService(
    private val context: Context,
    private val mEmvNfcKernelApi: EmvNfcKernelApi
): EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null
    private var mEmvListener: EmvListener? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        val emvModel = (arguments as? Map<*, *>)?.toEmvModel()
        if(emvModel != null) {
            mEmvListener = UrovoEmvListener()
            mEmvNfcKernelApi.setListener(mEmvListener)
            mEmvNfcKernelApi.setContext(context)

            mEmvNfcKernelApi.LogOutEnable(0)
            startKernel(ContantPara.CheckCardMode.SWIPE_OR_INSERT_OR_TAP, emvModel)
        }
    }

    override fun onCancel(arguments: Any?) {
        TODO("Not yet implemented")
    }

    private fun startKernel(
        checkCardMode: ContantPara.CheckCardMode,
        emvModel: EmvModel
    ) {
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

                data["enableBeeper"] = emvModel.enableBeeper // Enable beeper during EMV process
                data["enableTapSwipeCollision"] = emvModel.enableTapSwipeCollision // Enable tap and swipe collision handling
                mEmvNfcKernelApi.startKernel(data)
            } catch (e: Exception) {
                eventSink?.error(
                    "EmvServiceError",
                    "Failed to start EMV kernel: ${e.message}",
                    null
                )
            } finally {
                mEmvNfcKernelApi.setListener(null)
                mEmvListener = null
            }
        }
    }

    inner class UrovoEmvListener : EmvListener {
        override fun onRequestSetAmount() {
            TODO("Not yet implemented")
        }

        override fun onReturnCheckCardResult(
            p0: ContantPara.CheckCardResult?,
            p1: Hashtable<String, String>?
        ) {
            TODO("Not yet implemented")
        }

        override fun onRequestSelectApplication(p0: ArrayList<String>?) {
            TODO("Not yet implemented")
        }

        override fun onRequestPinEntry(p0: ContantPara.PinEntrySource?) {
            TODO("Not yet implemented")
        }

        override fun onRequestOfflinePinEntry(p0: ContantPara.PinEntrySource?, p1: Int) {
            TODO("Not yet implemented")
        }

        override fun onRequestConfirmCardno() {
            TODO("Not yet implemented")
        }

        override fun onRequestFinalConfirm() {
            TODO("Not yet implemented")
        }

        override fun onRequestOnlineProcess(p0: String?, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onReturnBatchData(p0: String?) {
            TODO("Not yet implemented")
        }

        override fun onReturnTransactionResult(p0: ContantPara.TransactionResult?) {
            TODO("Not yet implemented")
        }

        override fun onRequestDisplayText(p0: ContantPara.DisplayText?) {
            TODO("Not yet implemented")
        }

        override fun onRequestOfflinePINVerify(
            p0: ContantPara.PinEntrySource?,
            p1: Int,
            p2: Bundle?
        ) {
            TODO("Not yet implemented")
        }

        override fun onReturnIssuerScriptResult(p0: ContantPara.IssuerScriptResult?, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onNFCrequestTipsConfirm(p0: ContantPara.NfcTipMessageID?, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onReturnNfcCardData(p0: Hashtable<String, String>?) {
            TODO("Not yet implemented")
        }

        override fun onNFCrequestOnline() {
            TODO("Not yet implemented")
        }

        override fun onNFCrequestImportPin(p0: Int, p1: Int, p2: String?) {
            TODO("Not yet implemented")
        }

        override fun onNFCTransResult(p0: ContantPara.NfcTransResult?) {
            TODO("Not yet implemented")
        }

        override fun onNFCErrorInfor(p0: ContantPara.NfcErrMessageID?, p1: String?) {
            TODO("Not yet implemented")
        }

    }

}
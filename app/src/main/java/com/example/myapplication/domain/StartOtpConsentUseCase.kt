package com.example.myapplication.domain



import android.app.Activity
import com.example.myapplication.data.OtpConsentManager
import com.example.myapplication.ui.SmsConsentReceiver

class StartOtpConsentUseCase(
    private val manager: OtpConsentManager = OtpConsentManager()
) {

    fun execute(
        activity: Activity,
        listener: SmsConsentReceiver.Listener
    ) {
        manager.startListening(activity, listener)
    }

    fun unregister(activity: Activity) {
        manager.unregister(activity)
    }
}
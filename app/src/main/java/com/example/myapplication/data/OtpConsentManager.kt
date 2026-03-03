package com.example.myapplication.data


import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.example.myapplication.ui.SmsConsentReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever


class OtpConsentManager {

    private var receiver: SmsConsentReceiver? = null

    fun startListening(
        activity: Activity,
        listener: SmsConsentReceiver.Listener
    ) {

        receiver = SmsConsentReceiver(listener)

        SmsRetriever.getClient(activity)
            .startSmsUserConsent(null) // null = listen to any sender

        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        activity.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)

    }

    fun unregister(activity: Activity) {
        receiver?.let {
            activity.unregisterReceiver(it)
            receiver = null
        }
    }
}
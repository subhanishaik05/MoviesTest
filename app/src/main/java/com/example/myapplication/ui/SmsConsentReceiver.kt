package com.example.myapplication.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsConsentReceiver(
    private val listener: Listener
) : BroadcastReceiver() {

    interface Listener {
        fun onConsentIntent(consentIntent: Intent)
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {

            val extras: Bundle? = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when (status?.statusCode) {

                CommonStatusCodes.SUCCESS -> {

                    val consentIntent =
                        extras.getParcelable<Intent>(
                            SmsRetriever.EXTRA_CONSENT_INTENT
                        )

                    consentIntent?.let {
                        listener.onConsentIntent(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    // Handle timeout if needed
                }
            }
        }
    }
}
package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.domain.StartOtpConsentUseCase
import com.example.myapplication.ui.SmsConsentReceiver
import com.example.myapplication.ui.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private val otpUseCase = StartOtpConsentUseCase()

    private val consentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                val message =
                    result.data?.getStringExtra(
                        SmsRetriever.EXTRA_SMS_MESSAGE
                    )
                viewModel.extractOtp(message)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startOtpListening()

        setContent {
            OtpScreen(viewModel)
        }
    }

    private fun startOtpListening() {

        otpUseCase.execute(
            this,
            object : SmsConsentReceiver.Listener {
                override fun onConsentIntent(intent: Intent) {
                    consentLauncher.launch(intent)
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        otpUseCase.unregister(this)
    }
}

@Composable
fun OtpScreen(viewModel: LoginViewModel) {

    val otp by viewModel.otp.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = {
                if (it.length <= 6) {   // limit to 6 digits
                    viewModel.onOtpChanged(it)
                }
            },
            label = { Text("OTP") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )
    }
}
package com.verifai.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verifai.core.pub.CoreConfiguration
import com.verifai.core.pub.Verifai
import com.verifai.core.pub.VerifaiLogger
import com.verifai.core.pub.exceptions.LicenseNotValidException
import com.verifai.core.pub.listeners.ResultListener
import com.verifai.core.pub.result.CoreResult
import com.verifai.example.databinding.ActivityMainBinding
import com.verifai.nfc.pub.NfcConfiguration
import com.verifai.nfc.pub.VerifaiNfc
import com.verifai.nfc.pub.listeners.NfcResultListener
import com.verifai.nfc.pub.result.NfcResult

/**
 * This activity shows how to start the Core flow and the NFC (Core + NFC) flow.
 * In your own application, you'll implement only one of the two.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Verifai.logger = object : VerifaiLogger {
            private val tag = "v-example"

            override fun log(e: Throwable) {
                Log.e(tag, Log.getStackTraceString(e))
            }

            override fun log(event: String) {
                Log.i(tag, event)
            }
        }

        binding.startButton.setOnClickListener {
            start()
        }
        binding.startNfcButton.setOnClickListener {
            startNfc()
        }
    }

    /**
     * In this method Verifai will be initialized and started. The steps to start the Verifai SDK are the following:
     *
     * 1. Call Verifai.setLicense(licenseString) where the licenseString is the license that has been obtained
     *      from https://dashboard.verifai.com
     * 2. Call Verifai.startScan(params) Verifai will startScanning if it has received a valid license. It will throw
     *      an error when the license is invalid. Please catch this error.
     */
    private fun start() {
        val license = BuildConfig.verifaiLicense
        // Use the license that has been obtained from the Verifai dashboard
        Verifai.setLicense(this@MainActivity, license)

        Verifai.configure(
            CoreConfiguration(
                enableVisualInspection = true,
            )
        )
        Verifai.start(this@MainActivity, object : ResultListener {
            override fun onSuccess(result: CoreResult) {
                coreResult = result
                val intent = Intent(this@MainActivity, VerifaiResultActivity::class.java)
                startActivity(intent)
            }

            override fun onError(e: Throwable) {
                // We are sorry, something wrong happened.
                if (e is LicenseNotValidException) {
                    Log.d("Authentication", "Authentication failed")
                }
            }
        }, "kotlin-example-core")
    }

    private fun startNfc() {
        val license = BuildConfig.verifaiLicense
        VerifaiNfc.setLicense(this@MainActivity, license)
        VerifaiNfc.configure(
            CoreConfiguration(
                enableVisualInspection = true,
            ),
            NfcConfiguration()
        )
        VerifaiNfc.start(this@MainActivity, object : NfcResultListener {
            override fun onSuccess(result: NfcResult) {
                nfcResult = result
                val intent = Intent(this@MainActivity, VerifaiResultActivity::class.java)
                startActivity(intent)
            }

            override fun onError(e: Throwable) {
                // We are sorry, something wrong happened.
                if (e is LicenseNotValidException) {
                    Log.d("Authentication", "Authentication failed")
                }
            }
        }, "kotlin-example-nfc")
    }

    companion object {
        var coreResult: CoreResult? = null
        var nfcResult: NfcResult? = null
    }
}

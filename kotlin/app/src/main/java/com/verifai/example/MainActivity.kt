package com.verifai.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.verifai.core.Verifai
import com.verifai.core.VerifaiConfiguration
import com.verifai.core.exceptions.LicenceNotValidException
import com.verifai.core.listeners.VerifaiResultListener
import com.verifai.core.result.VerifaiResult
import kotlinx.android.synthetic.main.activity_main.*


/**
 * The MainActivity of this SDK example
 *
 * Starts the Verifai Example App and handles everything. Do not forget to set a valid licence.
 * A valid licence can be obtained from https://dashboard.verifai.com/
 *
 * @see "https://verifai.dev"
 * @author Igor Pidik - Verifai.com
 * @author Rutger Roffel - Verifai.com
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auto_button.setOnClickListener {
            start()
        }
    }

    /**
     * In this method Verifai will be initialized and started. The steps to start the Verifai SDK are the following:
     *
     * 1. Call Verifai.setLicence(licenceString) where the licenceString is the licence that has been obtained
     *      from https://dashboard.verifai.com
     * 2. Call Verifai.startScan(params) Verifai will startScanning if it has received a valid licence. It will throw
     *      an error when the licence is invalid. Please catch this error.
     */
    private fun start() {
        val licence = BuildConfig.verifaiLicence
        Verifai.setLicence(this@MainActivity, licence)

        val verifaiConfiguration = VerifaiConfiguration(show_instruction_screens = true)
        Verifai.configure(verifaiConfiguration)
        Verifai.startScan(this@MainActivity, object : VerifaiResultListener {
            override fun onSuccess(result: VerifaiResult) {
                verifaiResult = result
                // Go to the result screen
                val intent = Intent(this@MainActivity, VerifaiResultActivity::class.java)
                // Start your next activity
                startActivity(intent)
            }

            override fun onCanceled() {
                // Return to the main app
            }

            override fun onError(e: Throwable) {
                // We are sorry, something wrong happened.
                if (e is LicenceNotValidException) {
                    Log.d("Authentication", "Authentication failed")
                }
            }
        })
    }

    companion object {
        var verifaiResult: VerifaiResult? = null
    }
}

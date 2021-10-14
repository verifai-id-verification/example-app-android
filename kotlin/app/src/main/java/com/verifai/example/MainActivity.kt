package com.verifai.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verifai.core.*
import com.verifai.core.exceptions.LicenceNotValidException
import com.verifai.core.listeners.VerifaiResultListener
import com.verifai.core.result.VerifaiResult
import com.verifai.example.databinding.ActivityMainBinding

/**
 * The MainActivity of this SDK example
 *
 * Starts the Verifai Example App and handles everything. Do not forget to set a valid licence.
 * A valid licence can be obtained from https://dashboard.verifai.com/
 *
 * @see "https://verifai.dev"
 * @author Igor Pidik - Verifai.com
 * @author Jeroen Oomkes - Verifai.com
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    /**
     * In this method Verifai will be initialized and started. The steps to start the Verifai SDK are the following:
     *
     * 1. Call Verifai.setLicence(licenceString) where the licenceString is the licence that has been obtained
     *      from https://dashboard.verifai.com
     * 2. Call Verifai.startScan(params) Verifai will startScanning if it has received a valid licence. It will throw
     *      an error when the licence is invalid. Please catch this error.
     */
    fun start(view: View) {
        val licence = BuildConfig.verifaiLicence
        Verifai.setLicence(this@MainActivity, licence)
        Verifai.configure(getVerifaiConfiguration())
        Verifai.startScan(this@MainActivity, object : VerifaiResultListener {
            override fun onSuccess(result: VerifaiResult) {
                verifaiResult = result
                val intent = Intent(this@MainActivity, VerifaiResultActivity::class.java)
                startActivity(intent)
            }

            override fun onCanceled() {
                Log.d("Verifai", "Cancel")
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

    private fun getVerifaiConfiguration(): VerifaiConfiguration {
        return VerifaiConfiguration(
            show_instruction_screens = true,
            instructionScreenConfiguration = VerifaiInstructionScreenConfiguration(
                true,
                mapOf(
                    Pair(
                        VerifaiInstructionScreenId.AUTOMATIC_SCAN_FLOW_INSTRUCTION,
                        VerifaiSingleInstructionScreen(VerifaiInstructionType.HIDDEN)
                    )
                )
            )
        )
    }

    companion object {
        var verifaiResult: VerifaiResult? = null
    }
}

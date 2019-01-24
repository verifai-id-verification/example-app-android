package com.verifai.sdkexample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.verifai.*
import com.verifai.events.VerifaiLicenceListener
import com.verifai.events.VerifaiNeuralModelListener
import com.verifai.events.VerifaiResultListener
import com.verifai.exceptions.CriticalException
import com.verifai.exceptions.FatalException
import com.verifai.exceptions.fatal.LicenceNotValidException
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


/**
 * The MainActivity of this SDK example
 *
 * Starts the example app and handles everything.
 * @see "https://docs.verifai.com/android_docs/android-sdk-latest.html"
 * @author Igor Pidik - Verifai.com
 * @author Rutger Roffel - Verifai.com
 */
class MainActivity : AppCompatActivity() {
    companion object {
        var result: VerifaiResult? = null
    }

    /**
     * We initiate Verifai in this onCreate method just for this example. It might be wise to move
     * this to another method.
     *
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#authenticate-app
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addVerifaiLogger()

        val licence = "=== Verifai Licence file V2 ===\n" +
                "Enter the rest of the licence file over here. Get it from https://dashboard.verifai.com"

        Verifai.configure(licence, "sdk_example_android", object : VerifaiLicenceListener {
            override fun onConfigured() {
                // Handle success -> Verifai can now be started
                activateButtons()
            }

            override fun onError(e: LicenceNotValidException) {
                // Handle error
                Log.e("Error","The provided licence is invalid")
            }
        })
    }

    /**
     * It is possible to add a Verifai logger to log events and exceptions.
     */
    private fun addVerifaiLogger() {
        Verifai.logger = object:VerifaiLogger{
            override fun log(verifaiDebug: VerifaiDebug) {
                // handle verifaiDebug
            }

            override fun log(event: String) {
                Log.d("Event", event)
            }

            override fun log(e: Throwable) {
                Log.e("Error", e.stackTrace.toString())
            }
        }
    }

    /**
     * Enable the buttons in the MainActivity so the user can use Verifai.
     */
    private fun activateButtons() {
        auto_button.setOnClickListener {
            handleAutoButtonClick()
        }
        manual_button.setOnClickListener {
            goScanManual()
        }
        auto_button.isEnabled = true
        manual_button.isEnabled = true
    }

    /**
     * Download the Neural network if the user want to use the automatic mode.
     * Check if the AI mode is supported.
     */
    private fun handleAutoButtonClick() {
        if (Verifai.isAiModeSupported) {
            // AI mode is supported
            downloadNN()
        } else {
            // AI mode is not supported
            Log.d("Event","Automatic mode is not supported")
        }
    }

    /**
     * Download the neural model that is needed for the automatic mode.
     * Supported devices are those with at least Android 5 (Lollipop) and support for 64bit ABIs.
     *
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html?kotlin#ai-mode-2
     */
    private fun downloadNN() {
        val countryCodes = listOf("NL")
        val destinationDirectory: File? = null // Optional, will be downloaded to cache by default
        Toast.makeText(this@MainActivity, "Downloading started", Toast.LENGTH_LONG).show()
        Verifai.downloadNeuralModel(this, countryCodes, destinationDirectory, object : VerifaiNeuralModelListener {
            override fun onProgress(progress: Int) {
                // Handle progress. Always notify the user know that the system is still working.
            }

            override fun onInitialized() {
                // Handle success
                Toast.makeText(this@MainActivity, "Downloading finished", Toast.LENGTH_SHORT).show()
                goScanAutomatic()
            }

            override fun onError(e: Throwable) {
                Log.e("Exception", e.stackTrace.toString())
            }
        })
    }

    /**
     * Enter the automatic scan flow. Please handle all exceptions for the best user experience.
     *
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#ai-mode-2
     */
    private fun goScanAutomatic() {
        Verifai.start(this, VerifaiScanMode.AI, object : VerifaiResultListener {
            override fun onResult(result: VerifaiResult) {
                // Handle result
                showResultActivity(result)
            }

            override fun onUpdateAvailable() {
                // This will be called when there is a new version of the SDK available. The scanning will continue.
                // You could log to your central logging to be notified of outdated version.
            }

            override fun onError(e: Throwable) {
                // Handle errors
                when (e) {
                    is FatalException -> {
                        // Verifai can not start
                    }
                    is CriticalException -> {
                        // AI mode will not work. It is possible to start the Manual mode.
                        auto_button.isEnabled = false
                    }
                    else -> {
                        // Network problems during scanning
                    }
                }
            }
        })
    }

    /**
     * Enter the manual flow. Please handle all exceptions for the best user experience.
     * 
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#manual-mode-2
     */
    private fun goScanManual() {
        Verifai.start(this, VerifaiScanMode.MANUAL, object : VerifaiResultListener {
            override fun onResult(result: VerifaiResult) {
                // Handle result
                showResultActivity(result)
            }

            override fun onUpdateAvailable() {
                // This will be called when there is a new version of the SDK available, but the scanning will continue
                // You could log to your central logging to be notified of outdated version
            }

            override fun onError(e: Throwable) {
                // Handle errors
                when (e) {
                    is FatalException -> {
                        // Verifai has not been configured
                    }
                    else -> {
                        // Ignore network problems during scanning
                    }
                }
            }
        })
    }

    /**
     * Shows the result of the scan. It is possible to expand this with all the properties of VerifaiResult
     *
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html?kotlin#handling-the-result
     * @param: result: VerifaiResult - The result with information to show
     */
    private fun showResultActivity(result: VerifaiResult) {
        // Show activity with the scan result
        val intent = Intent(this@MainActivity, ScanResultActivity::class.java)
        MainActivity.result = result
        startActivity(intent)
    }
}

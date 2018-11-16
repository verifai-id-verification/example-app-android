package com.verifai.sdkexample


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.verifai.Verifai
import com.verifai.VerifaiResult
import com.verifai.check.VerifaiSecurityFeaturesResult
import com.verifai.events.VerifaiNfcResultListener
import com.verifai.events.VerifaiSecurityFeaturesListener
import com.verifai.events.VerifaiVizListener
import com.verifai.exceptions.check.SecurityFeaturesDownloadFailedException
import com.verifai.exceptions.check.SecurityFeaturesNotFoundException
import com.verifai.exceptions.check.SecurityFeaturesParameterNotValidException
import com.verifai.exceptions.nfc.NfcDisabledException
import com.verifai.exceptions.nfc.NoNfcException
import com.verifai.exceptions.viz.VizNoZonesException
import com.verifai.nfc.VerifaiNfcResult
import com.verifai.viz.VerifaiVizResult
import kotlinx.android.synthetic.main.activity_scan_result.*
import java.text.DateFormat

class ScanResultActivity : AppCompatActivity() {

    private var nfcResult: VerifaiNfcResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        showResult()

    }

    private fun showResult() {
        button_viz_check.isEnabled = false
        MainActivity.result?.let {
            val result = it
            front_image.setImageBitmap(result.frontImage)

            mrz.text = result.mrzData?.raw
            names.text = result.mrzData?.names ?: "???"
            surname.text = result.mrzData?.surname ?: "???"
            if (result.mrzData?.dateOfBirth != null) {
                dob.text = DateFormat.getDateInstance().format(result.mrzData?.dateOfBirth)
            } else {
                dob.text = getString(R.string.unknown)
            }

            button_new_scan.setOnClickListener {
                finish()
            }

            button_security_features.setOnClickListener {
                runSecurityFeaturesCheck(result)
            }

            button_nfc_scan.setOnClickListener {
                scanNFC(result)
            }

            button_viz_check.setOnClickListener {
                nfcResult?.let {
                    runVIZCheck(result, it)
                }
            }


        }
    }

    private fun scanNFC(verifaiResult: VerifaiResult) {
        Verifai.readNfc(
                this@ScanResultActivity,
                verifaiResult,
                resultListener = object : VerifaiNfcResultListener {
                    override fun onProgress(progress: Int) {
                        // from 0 to 100
                    }

                    override fun onResult(result: VerifaiNfcResult) {
                        Toast.makeText(this@ScanResultActivity, "NFC read!", Toast.LENGTH_LONG).show()
                        nfcResult = result
                        button_viz_check.isEnabled = true

                    }

                    override fun onError(e: Throwable) {
                        if (e is NfcDisabledException) {
                            // Show enable nfc dialog
                            runOnUiThread {
                                if (!isFinishing) {
                                    AlertDialog.Builder(this@ScanResultActivity)
                                            .setTitle("NFC Disabled")
                                            .setMessage("Please enable NFC.")
                                            .setCancelable(false)
                                            .setPositiveButton(android.R.string.ok) { dialog, which ->
                                                // ok
                                            }.show()
                                }
                            }
                        } else if (e is NoNfcException) {
                            // Show enable nfc dialog
                            runOnUiThread {
                                if (!isFinishing) {
                                    AlertDialog.Builder(this@ScanResultActivity)
                                            .setTitle("No NFC support")
                                            .setMessage("This device does not support NFC.")
                                            .setCancelable(false)
                                            .setPositiveButton(android.R.string.ok) { dialog, which ->
                                                // ok
                                            }.show()
                                }
                            }
                        }

                        Verifai.logger?.log(e)
                    }
                })
    }


    private fun runSecurityFeaturesCheck(verifaiResult: VerifaiResult) {
        Verifai.checkSecurityFeatures(
                context = this,
                verifaiResult = verifaiResult,
                scoreThreshold = 1f,
                listener = object : VerifaiSecurityFeaturesListener {
                    override fun onResult(result: VerifaiSecurityFeaturesResult) {
                        Toast.makeText(this@ScanResultActivity, "Security feature check score: " + result.score.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        if (e is SecurityFeaturesNotFoundException) {
                            // Handle exception
                        }
                        if (e is SecurityFeaturesDownloadFailedException) {
                            // Handle exception
                        }
                        if (e is SecurityFeaturesParameterNotValidException) {
                            // Handle exception
                        }
                    }
                })
    }

    private fun runVIZCheck(verifaiResult: VerifaiResult, verifaiNfcResult: VerifaiNfcResult) {
        Verifai.checkViz(this,
                verifaiResult = verifaiResult,
                verifaiNfcResult = verifaiNfcResult,
                listener = object : VerifaiVizListener {
                    override fun onResult(result: VerifaiVizResult) {
                        // Handle Result
                    }

                    override fun onError(e: Throwable) {
                        Verifai.logger?.log(e)
                        if (e is VizNoZonesException) {
                            // Handle exception
                        }
                    }
                })
    }

}

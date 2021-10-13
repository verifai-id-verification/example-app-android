package com.verifai.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verifai.core.Verifai
import com.verifai.example.databinding.ActivityVerifaiResultBinding
import com.verifai.liveness.VerifaiLiveness
import com.verifai.liveness.VerifaiLivenessCheckListener
import com.verifai.liveness.checks.CloseEyes
import com.verifai.liveness.checks.FaceMatching
import com.verifai.liveness.checks.Tilt
import com.verifai.liveness.result.VerifaiLivenessCheckResults
import com.verifai.manual_data_crosscheck.VerifaiManualDataCrossCheck
import com.verifai.manual_data_crosscheck.listeners.VerifaiManualDataCrossCheckListener
import com.verifai.manual_data_crosscheck.results.VerifaiManualDataCrossCheckResult
import com.verifai.manual_security_features_check.VerifaiManualSecurityFeaturesCheck
import com.verifai.manual_security_features_check.exceptions.SecurityFeaturesNotFoundException
import com.verifai.manual_security_features_check.listeners.VerifaiManualSecurityFeaturesCheckListener
import com.verifai.manual_security_features_check.results.VerifaiManualSecurityFeaturesResult
import com.verifai.nfc.VerifaiNfc
import com.verifai.nfc.VerifaiNfcResultListener
import com.verifai.nfc.result.VerifaiNfcResult

class VerifaiResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifaiResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifaiResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /**
         * Start the NFC process based on the scan result.
         */
        binding.contentResult.startNfcButton.setOnClickListener {
            val nfcListener = object : VerifaiNfcResultListener {
                override fun onResult(result: VerifaiNfcResult) {
                    Verifai.logger?.log("NFC completed")
                }

                override fun onCanceled() {
                    Verifai.logger?.log("NFC has been canceled")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            }

            MainActivity.verifaiResult?.let {
                VerifaiNfc.start(this, it, true, nfcListener, true)
            }
        }

        /**
         * Start the Manual Data Crosscheck based on the scan result.
         */
        binding.contentResult.startManualDataCrosscheckButton.setOnClickListener {
            VerifaiManualDataCrossCheck.start(
                this,
                MainActivity.verifaiResult!!,
                object : VerifaiManualDataCrossCheckListener {
                    override fun onResult(result: VerifaiManualDataCrossCheckResult) {
                        Verifai.logger?.log("Manual Data Crosscheck Completed")
                    }

                    override fun onCanceled() {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        }

        /**
         * Start the Manual Security Features Check based on the scan result.
         */
        binding.contentResult.startManualSecurityFeaturesCheckButton.setOnClickListener {
            VerifaiManualSecurityFeaturesCheck.start(
                this,
                MainActivity.verifaiResult,
                object : VerifaiManualSecurityFeaturesCheckListener {
                    override fun onResult(result: VerifaiManualSecurityFeaturesResult) {
                        Verifai.logger?.log("Manual Security Features Completed")
                    }

                    override fun onCanceled() {

                    }

                    override fun onError(e: Throwable) {
                        if (e is SecurityFeaturesNotFoundException) {
                            Verifai.logger?.log("This document does not have any security features.")
                        }
                    }

                })
        }

        /**
         * Start the Liveness Check. A scan result is only needed for the face match. Without the
         * face match the liveness check can also run separately.
         */
        binding.contentResult.startLivenessButton.setOnClickListener {
            VerifaiLiveness.clear(this)
            VerifaiLiveness.start(this,
                arrayListOf(
                    FaceMatching(this, MainActivity.verifaiResult?.frontImage!!),
                    CloseEyes(this), Tilt(this, -25)
                ), object : VerifaiLivenessCheckListener {
                    override fun onResult(results: VerifaiLivenessCheckResults) {
                        Log.d(TAG, "Done")
                        for (result in results.resultList) {
                            Log.d(TAG, "%s finished".format(result.check.instruction))
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        }

        binding.contentResult.mrzValue.text = MainActivity.verifaiResult?.mrzData?.mrzString
        binding.contentResult.firstNameValue.text = MainActivity.verifaiResult?.mrzData?.firstName
        binding.contentResult.lastNameValue.text = MainActivity.verifaiResult?.mrzData?.lastName
    }

    companion object {
        private const val TAG = "RESULT_ACTIVITY"
    }
}

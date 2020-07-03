package com.verifai.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verifai.core.Verifai
import com.verifai.liveness.VerifaiLiveness
import com.verifai.liveness.VerifaiLivenessCheckListener
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
import kotlinx.android.synthetic.main.activity_verifai_result.*
import kotlinx.android.synthetic.main.content_verifai_result.*

class VerifaiResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifai_result)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("result", MainActivity.verifaiResult?.document.toString())

        /**
         * Start the NFC process based on the scan result.
         */
        start_nfc_button.setOnClickListener {
            val nfcListener = object : VerifaiNfcResultListener {
                override fun onResult(result: VerifaiNfcResult) {
                    Verifai.logger?.log("NFC Completed")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            }

            MainActivity.verifaiResult?.let {
                VerifaiNfc.start(this, it, true, nfcListener, false)
            }
        }

        /**
         * Start the Manual Data Crosscheck based on the scan result.
         */
        start_manual_data_crosscheck_button.setOnClickListener {
            VerifaiManualDataCrossCheck.start(this, MainActivity.verifaiResult!!, object : VerifaiManualDataCrossCheckListener {
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
        start_manual_security_features_check_button.setOnClickListener {
            VerifaiManualSecurityFeaturesCheck.start(this, MainActivity.verifaiResult!!, object : VerifaiManualSecurityFeaturesCheckListener {
                override fun onResult(result: VerifaiManualSecurityFeaturesResult) {
                    Verifai.logger?.log("Manual Security Features Completed")
                }

                override fun onCanceled() {

                }

                override fun onError(e: Throwable) {
                    if(e is SecurityFeaturesNotFoundException) {
                        Verifai.logger?.log("This document does not have any security features.")
                    }
                }

            })
        }

        /**
         * Start the Liveness Check. A scan result is not needed. So the liveness check can also run
         * separately.
         */
        start_liveness_button.setOnClickListener {
            VerifaiLiveness.start(this, null, object : VerifaiLivenessCheckListener {
                override fun onResult(results: VerifaiLivenessCheckResults) {
                    Log.d("results", "done")
                    for (result in results.resultList) {
                        Log.d("result", "%s finished".format(result.check.instruction))
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        }

        mrz_value.text = MainActivity.verifaiResult?.mrzData?.mrzString ?: "???"
        first_name_value.text = MainActivity.verifaiResult?.mrzData?.firstName ?: "???"
        last_name_value.text = MainActivity.verifaiResult?.mrzData?.lastName ?: "???"
    }
}

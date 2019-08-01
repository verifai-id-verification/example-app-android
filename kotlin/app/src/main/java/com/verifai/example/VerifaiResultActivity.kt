package com.verifai.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.verifai.core.Verifai
import com.verifai.liveness.VerifaiLiveness
import com.verifai.liveness.VerifaiLivenessCheckListener
import com.verifai.liveness.result.VerifaiLivenessCheckResults
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

        start_nfc_button.setOnClickListener {
            val nfcListener = object : VerifaiNfcResultListener {
                override fun onResult(result: VerifaiNfcResult) {
                    Verifai.logger?.log("NFC Completed")
                }

                override fun onProgress(progress: Int) {

                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            }

            MainActivity.verifaiResult?.let {
                VerifaiNfc.start(this, it, true, nfcListener)
            }
        }

        start_liveness_button.setOnClickListener {
            VerifaiLiveness.start(this, null, object : VerifaiLivenessCheckListener {
                override fun onResult(results: VerifaiLivenessCheckResults) {
                    Log.d("results", "done")
                    for(result in results.resultList) {
                        Log.d("result", result.check.instruction)
                        Log.d("result", result.check.status.toString())
                    }


                }


                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        }

        mrz_value.text = MainActivity.verifaiResult?.mrzData?.mrzString ?: "???"
        first_name_value.text = MainActivity.verifaiResult?.mrzData?.firstName?: "???"
        last_name_value.text = MainActivity.verifaiResult?.mrzData?.lastName ?: "???"

    }
}

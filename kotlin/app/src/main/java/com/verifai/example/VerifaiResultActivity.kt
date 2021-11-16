package com.verifai.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verifai.core.Verifai
import com.verifai.example.databinding.ActivityVerifaiResultBinding
import com.verifai.liveness.VerifaiLiveness
import com.verifai.liveness.VerifaiLivenessCheckListener
import com.verifai.liveness.checks.CloseEyes
import com.verifai.liveness.checks.FaceMatching
import com.verifai.liveness.checks.Tilt
import com.verifai.liveness.result.VerifaiFaceMatchingCheckResult
import com.verifai.liveness.result.VerifaiLivenessCheckResults
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
                            Log.d(TAG, "%s status".format(result.status))
                            if (result is VerifaiFaceMatchingCheckResult) {
                                Log.d(TAG, "Face match?: ${result.match}")
                                result.confidence?.let {
                                    Log.d(TAG, "Face match confidence ${(it * 100).toInt()}%")
                                }
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                }
            )
        }

        binding.contentResult.mrzValue.text = MainActivity.verifaiResult?.mrzData?.mrzString
        binding.contentResult.firstNameValue.text = MainActivity.verifaiResult?.mrzData?.firstName
        binding.contentResult.lastNameValue.text = MainActivity.verifaiResult?.mrzData?.lastName

        MainActivity.verifaiResult?.visualInspectionZoneResult.also { map ->
            binding.vizDetailsBtn.setOnClickListener {
                val intent = Intent(this, GeneralResultActivity::class.java)
                intent.putExtra("title", "VIZ result")
                val res: HashMap<String, String> = HashMap()
                map?.forEach {
                    res[it.key] = it.value
                }
                intent.putExtra("result", res)
                startActivity(intent)
            }
        } ?: run {
            binding.vizDetailsBtn.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "RESULT_ACTIVITY"
    }
}

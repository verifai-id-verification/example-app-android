package com.verifai.example

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verifai.example.databinding.ActivityVerifaiResultBinding
import com.verifai.liveness.pub.LivenessCheckListener
import com.verifai.liveness.pub.VerifaiLiveness
import com.verifai.liveness.pub.checks.CloseEyes
import com.verifai.liveness.pub.checks.FaceMatching
import com.verifai.liveness.pub.checks.Tilt
import com.verifai.liveness.pub.result.FaceMatchingCheckResult
import com.verifai.liveness.pub.result.LivenessCheckResults

/**
 * This activity display the results from either the Core or NFC flow.
 * In your own application you'll implement only one of the two flows,
 * this is just an example to demonstrate both cases.
 */
class VerifaiResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifaiResultBinding

    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifaiResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // If NFC has been read, use faceImage from chip
        image = MainActivity.nfcResult?.faceImage
        if (image == null) {
            // Otherwise the document front image scan
            image = MainActivity.coreResult?.frontImage
        }

        /**
         * Start the Liveness Check. A scan result is only needed for the face match. Without the
         * face match the liveness check can also run separately.
         */
        binding.contentResult.startLivenessButton.setOnClickListener {
            VerifaiLiveness.clear(this)

            val checks = arrayListOf(
                CloseEyes(),
                Tilt(-25)
            )

            // Add Face match check with either NFC face image, or document front image
            image?.let {
                checks.add(FaceMatching(it))
            }

            VerifaiLiveness.start(
                this, object : LivenessCheckListener {
                    override fun onResult(results: LivenessCheckResults) {
                        Log.d(TAG, "Done")
                        for (result in results.resultList) {
                            Log.d(TAG, "%s finished".format(result.check.instruction))
                            Log.d(TAG, "%s status".format(result.status))
                            if (result is FaceMatchingCheckResult) {
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
                },
                checks
            )
        }

        // Display MRZ results from either the Core or NFC flow:
        var mrzResult = MainActivity.coreResult?.mrzData
        // In case of the NFC flow:
        if (mrzResult == null) {
            mrzResult = MainActivity.nfcResult?.mrzData
        }
        mrzResult?.let {
            binding.contentResult.mrzValue.text = mrzResult.mrzString
            binding.contentResult.firstNameValue.text = mrzResult.givenNames
            binding.contentResult.lastNameValue.text = mrzResult.surname
        }

        image.let {
            binding.contentResult.image.setImageBitmap(it)
        }
    }

    companion object {
        private const val TAG = "RESULT_ACTIVITY"
    }
}

package com.verifai.sdkexample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verifai.Verifai
import com.verifai.VerifaiResult
import com.verifai.check.VerifaiSecurityFeaturesResult
import com.verifai.events.VerifaiSecurityFeaturesListener
import com.verifai.exceptions.check.SecurityFeaturesDownloadFailedException
import com.verifai.exceptions.check.SecurityFeaturesNotFoundException
import com.verifai.exceptions.check.SecurityFeaturesParameterNotValidException
import kotlinx.android.synthetic.main.fragment_security_features.*

/**
 * Runs Security features check and shows the results to the user
 */
class SecurityFeaturesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security_features, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.result?.let {
            runSecurityFeaturesCheck(it)
        }
    }

    /**
     * Checks security features
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#security-features-check
     */
    private fun runSecurityFeaturesCheck(verifaiResult: VerifaiResult) {
        activity?.let {
            Verifai.checkSecurityFeatures(
                    context = it,
                    verifaiResult = verifaiResult,
                    scoreThreshold = 1f,
                    listener = object : VerifaiSecurityFeaturesListener {
                        override fun onResult(result: VerifaiSecurityFeaturesResult) {
                            showResult(result)
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

    }


    private fun showResult(result: VerifaiSecurityFeaturesResult) {
        val (maxScore, score, threshold) = result
        max_score_text.text = maxScore.toString()
        score_text.text = score.toString()
        threshold_text.text = threshold.toString()
    }


}

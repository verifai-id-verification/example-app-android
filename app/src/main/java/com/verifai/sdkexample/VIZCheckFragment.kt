package com.verifai.sdkexample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verifai.Verifai
import com.verifai.VerifaiResult
import com.verifai.events.VerifaiVizListener
import com.verifai.exceptions.viz.VizNoZonesException
import com.verifai.nfc.VerifaiNfcResult
import com.verifai.viz.VerifaiVizResult
import kotlinx.android.synthetic.main.fragment_vizcheck.*


class VIZCheckFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vizcheck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val verifaiResult = MainActivity.result
        val verifaiNfcResult = ScanResultActivity.nfcResult

        // both VerifaiResult and VerifaiNfcResult are necessary for a VIZ check
        if (verifaiResult != null && verifaiNfcResult != null) {
            runVIZCheck(verifaiResult, verifaiNfcResult)
        }
    }

    /**
     * Runs the VIZ check
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#starting-the-viz-check
     */
    private fun runVIZCheck(verifaiResult: VerifaiResult, verifaiNfcResult: VerifaiNfcResult) {
        Verifai.checkViz(activity!!,
                verifaiResult = verifaiResult,
                verifaiNfcResult = verifaiNfcResult,
                listener = object : VerifaiVizListener {
                    override fun onResult(result: VerifaiVizResult) {
                        showResult(result)
                    }

                    override fun onError(e: Throwable) {
                        Verifai.logger?.log(e)
                        if (e is VizNoZonesException) {
                            // Handle exception
                        }
                    }
                })
    }

    /**
     * Shows the result to the user,
     * all the properties in the VerifaiVizResult are nullable since only random 3 are always checked
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#starting-the-viz-check
     */
    private fun showResult(result: VerifaiVizResult) {
        val notChecked = "Not Checked"
        passed_all.text = result.passedAll?.toString()?: notChecked
        passed_photo.text = result.passedPhoto?.toString()?: notChecked
        passed_date_of_birth.text = result.passedDateOfBirth?.toString()?: notChecked
        passed_date_of_expiry.text = result.passedDateOfExpiry?.toString()?: notChecked
        passed_sex.text = result.passedSex?.toString()?: notChecked
        passed_issuing_country.text = result.passedIssuingCountry?.toString()?: notChecked
        passed_surname.text = result.passedSurname?.toString()?: notChecked
        passed_given_name.text = result.passedGivenName?.toString()?: notChecked
    }
}

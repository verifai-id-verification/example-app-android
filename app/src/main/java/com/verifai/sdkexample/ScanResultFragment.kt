package com.verifai.sdkexample

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_document_scan_result.*
import java.text.DateFormat

/**
 * After successful scan, this fragment shows retrieved data to the user
 */
class ScanResultFragment : Fragment() {
    private var callback: CanSwitchFragments? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_scan_result, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showResult()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as CanSwitchFragments
    }

    /**
     * Shows the retrieved data and options to work further with the VerifaiResult
     * The VerifaiResult is needed to check the security features, NFC read, VIZ check
     */
    private fun showResult() {
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

            // start a new scan
            button_new_scan.setOnClickListener {
                activity?.finish()
            }

            // run security features check
            button_security_features.setOnClickListener {
                callback?.switchFragment(SecurityFeaturesFragment())
            }

            // read NFC
            button_nfc_scan.setOnClickListener {
                callback?.switchFragment(NfcFragment())
            }

            // run VIZ check
            button_viz_check.setOnClickListener {
                // VerifaiNfcResult is necessary for a VIZ check
                if(ScanResultActivity.nfcResult != null) {
                    callback?.switchFragment(VIZCheckFragment())
                } else {
                    Toast.makeText(activity, "Scan NFC first", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

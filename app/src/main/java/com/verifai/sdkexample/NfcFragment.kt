package com.verifai.sdkexample


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verifai.Verifai
import com.verifai.VerifaiResult
import com.verifai.events.VerifaiNfcResultListener
import com.verifai.exceptions.nfc.NfcDisabledException
import com.verifai.exceptions.nfc.NoNfcException
import com.verifai.nfc.VerifaiNfcResult
import kotlinx.android.synthetic.main.fragment_document_read_nfc.*

/**
 * This fragment handles reading of the NFC and showing the result to the User
 */
class NfcFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_read_nfc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // We cannot start NFC read without VerifaiResult from previous scanning
        MainActivity.result?.let {
            readNFC(it)
        }
    }

    /**
     * Reads the NFC
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#nfc-emrtd-check
     */
    private fun readNFC(verifaiResult: VerifaiResult) {
        Verifai.readNfc(
                activity!!,
                verifaiResult,
                resultListener = object : VerifaiNfcResultListener {
                    override fun onProgress(progress: Int) {
                        // from 0 to 100
                    }

                    override fun onResult(result: VerifaiNfcResult) {
                        ScanResultActivity.nfcResult = result
                        showResult(result)
                    }

                    override fun onError(e: Throwable) {
                        if (e is NfcDisabledException) {
                            // Show enable nfc dialog
                            activity?.runOnUiThread {
                                if (!activity!!.isFinishing) {
                                    AlertDialog.Builder(activity!!)
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
                            activity?.runOnUiThread {
                                if (!activity!!.isFinishing) {
                                    AlertDialog.Builder(activity!!)
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

    /**
     * Shows all read and processed data from NFC to the user
     * @see: https://docs.verifai.com/android_docs/android-sdk-latest.html#nfc-result
     */
    private fun showResult(result: VerifaiNfcResult) {
        result.bitmap?.let {
            nfc_image.setImageBitmap(it)
        }

        result.mrzText?.let {
            nfc_mrz.text = it
        }

        nfc_mrz_match.text = result.mrzMatch.toString()
        nfc_com_sod_match.text = result.comSodMatch.toString()
        nfc_bac_status.text = result.bacStatus.toString()
        nfc_aa_status.text = result.activeAuthenticationStatus.toString()
        nfc_ca_status.text = result.chipAuthenticationStatus.toString()
        nfc_valid_document_signer.text = result.validDocumentSigner.toString()
        nfc_valid_certificate.text = result.validCertificate.toString()
        nfc_valid_root_certificate.text = result.validRootCertificate.toString()
        nfc_scan_completed.text = result.scanCompleted.toString()
    }


}

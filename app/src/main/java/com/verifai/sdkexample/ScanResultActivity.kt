package com.verifai.sdkexample


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.verifai.nfc.VerifaiNfcResult


/**
 * This activity is created after successful scan, manages the result fragments
 * It implements CanSwitchFragments interface, for communication between Fragments and the activity
 */
class ScanResultActivity : AppCompatActivity(), CanSwitchFragments {
    companion object {
        var nfcResult: VerifaiNfcResult? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)

        // show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, ScanResultFragment())
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // handle back button in the action bar
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}

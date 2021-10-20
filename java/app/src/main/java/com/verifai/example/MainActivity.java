package com.verifai.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.annotation.NonNull;

import com.verifai.core.Verifai;
import com.verifai.core.VerifaiConfiguration;
import com.verifai.core.VerifaiLogger;
import com.verifai.core.listeners.VerifaiResultListener;
import com.verifai.core.result.VerifaiResult;
import com.verifai.liveness.VerifaiLiveness;
import com.verifai.liveness.VerifaiLivenessCheckListener;
import com.verifai.liveness.checks.CloseEyes;
import com.verifai.liveness.checks.FaceMatching;
import com.verifai.liveness.checks.Tilt;
import com.verifai.liveness.checks.VerifaiLivenessCheck;
import com.verifai.liveness.result.VerifaiLivenessCheckResults;
import com.verifai.manual_data_crosscheck.VerifaiManualDataCrossCheck;
import com.verifai.manual_data_crosscheck.listeners.VerifaiManualDataCrossCheckListener;
import com.verifai.manual_data_crosscheck.results.VerifaiManualDataCrossCheckResult;
import com.verifai.manual_security_features_check.VerifaiManualSecurityFeaturesCheck;
import com.verifai.manual_security_features_check.listeners.VerifaiManualSecurityFeaturesCheckListener;
import com.verifai.manual_security_features_check.results.VerifaiManualSecurityFeaturesResult;
import com.verifai.nfc.VerifaiNfc;
import com.verifai.nfc.VerifaiNfcResultListener;
import com.verifai.nfc.result.VerifaiNfcResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends Activity {
    private VerifaiResult result;

    public MainActivity() {
        result = null;
    }

    /**
     * Start the activity and initialize
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String licence = BuildConfig.verifaiLicence;
        Verifai.setLicence(this, licence); // The licence string that has been obtained from the dashboard.

        // Optional: Attach a Logger
        Verifai.logger = new VerifaiLogger() {
            @Override
            public void log(@NotNull Throwable throwable) {

            }

            @Override
            public void log(@NotNull String s) {

            }
        };

        this.findViewById(R.id.start_verifai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        this.findViewById(R.id.start_liveness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveness();
            }
        });
    }


    /**
     * Start the Verifai scan process
     */
    private void start() {
        VerifaiConfiguration configuration = new VerifaiConfiguration();
        configuration.setScanDuration(5.0);
        Verifai.configure(configuration);
        VerifaiResultListener resultListener = new VerifaiResultListener() {
            @Override
            public void onCanceled() {

            }

            @Override
            public void onSuccess(@NonNull VerifaiResult verifaiResult) {
                result = verifaiResult;
                showNfcButton();
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                Log.d("error", throwable.getMessage());
            }
        };
        Verifai.startScan(this, resultListener);
    }

    /**
     * Start the Verifai NFC process
     *
     * @param context The current context
     */
    private void startNfc(Context context) {
        VerifaiNfcResultListener nfcResultListener = new VerifaiNfcResultListener() {
            @Override
            public void onResult(@NotNull VerifaiNfcResult verifaiNfcResult) {

            }

            @Override
            public void onCanceled() {

            }

            @Override
            public void onError(@NotNull Throwable throwable) {

            }
        };
        if (result != null) {
            VerifaiNfc.start(context, result, true, nfcResultListener, true);
        }
    }

    /**
     * Start the Verifai Liveness Check process
     */
    private void startLiveness() {
        if (VerifaiLiveness.isLivenessCheckSupported(getBaseContext())) {
            VerifaiLivenessCheckListener livenessResultListener = new VerifaiLivenessCheckListener() {
                @Override
                public void onResult(@NotNull VerifaiLivenessCheckResults verifaiLivenessCheckResults) {

                }

                @Override
                public void onError(@NotNull Throwable throwable) {

                }
            };

            ArrayList<VerifaiLivenessCheck> checks = new ArrayList<>();
            if (result != null) {
                checks.add(new FaceMatching(this, Objects.requireNonNull(result.getFrontImage())));
            }
            checks.add(new Tilt(this, -25));
            checks.add(new CloseEyes(this, 2));

            VerifaiLiveness.start(this, checks, livenessResultListener);
        } else {
            // Sorry, the Liveness check is not supported by this device
        }
    }

    /**
     * Start the Manual Security Features Check process
     */
    private void startSecurityFeaturesCheck(VerifaiResult verifaiResult) {
        VerifaiManualSecurityFeaturesCheckListener verifaiManualSecurityFeaturesCheckListener = new VerifaiManualSecurityFeaturesCheckListener() {

            @Override
            public void onResult(@NotNull VerifaiManualSecurityFeaturesResult verifaiManualSecurityFeaturesResult) {

            }

            @Override
            public void onError(@NotNull Throwable throwable) {

            }

            @Override
            public void onCanceled() {

            }
        };

        VerifaiManualSecurityFeaturesCheck.start(this, verifaiResult, verifaiManualSecurityFeaturesCheckListener, 80);
    }


    /**
     * Start the Verifai Manual Security Features Check process
     *
     * @param verifaiResult The result from the core
     */
    private void startManualDataCrossCheck(VerifaiResult verifaiResult) {

        VerifaiManualDataCrossCheckListener verifaiManualDataCrossCheckListener = new VerifaiManualDataCrossCheckListener() {

            @Override
            public void onResult(@NotNull VerifaiManualDataCrossCheckResult verifaiManualDataCrossCheckResult) {

            }

            @Override
            public void onError(@NotNull Throwable throwable) {

            }

            @Override
            public void onCanceled() {

            }
        };

        VerifaiManualDataCrossCheck.start(this, verifaiResult, verifaiManualDataCrossCheckListener, null, 5);
    }

    /**
     * Show the NFC button when needed
     */
    private void showNfcButton() {
        this.findViewById(R.id.start_nfc).setVisibility(View.VISIBLE);
        this.findViewById(R.id.start_nfc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNfc(getBaseContext());
            }
        });
    }
}
